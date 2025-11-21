package com.xuno.payment.payment.service.impl;

import com.xuno.payment.common.exception.ResourceNotFoundException;
import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import com.xuno.payment.exchangeconfig.service.ExchangeConfigService;
import com.xuno.payment.payment.mapper.PaymentMapper;
import com.xuno.payment.payment.model.dto.PaymentDetailResponse;
import com.xuno.payment.payment.model.dto.PaymentRequest;
import com.xuno.payment.payment.model.dto.PaymentResponse;
import com.xuno.payment.payment.model.dto.StatusUpdateRequest;
import com.xuno.payment.payment.model.entity.Payment;
import com.xuno.payment.payment.model.enums.PaymentStatus;
import com.xuno.payment.payment.model.enums.UserRole;
import com.xuno.payment.payment.model.valueobject.StatusHistory;
import com.xuno.payment.payment.repository.PaymentRepository;
import com.xuno.payment.payment.repository.PaymentSpecification;
import com.xuno.payment.payment.service.EncryptionService;
import com.xuno.payment.payment.service.ExchangeRateService;
import com.xuno.payment.payment.service.FeeCalculationService;
import com.xuno.payment.payment.service.PaymentService;
import com.xuno.payment.payment.service.SystemVerificationService;
import com.xuno.payment.payment.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final ExchangeConfigService exchangeConfigService;
    private final ExchangeRateService exchangeRateService;
    private final FeeCalculationService feeCalculationService;
    private final SystemVerificationService systemVerificationService;
    private final EncryptionService encryptionService;
    private final MongoTemplate mongoTemplate;

    @Override
    public PaymentResponse create(PaymentRequest request, String adminId) {
        log.info("Creating payment: {} to {} for amount {}", 
                request.getSourceCurrency(), request.getTargetCurrency(), request.getSourceAmount());

        checkIdempotency(request.getIdempotencyKey());
        systemVerificationService.performInitialVerification(request);

        ExchangeRateConfiguration exchangeConfig = exchangeConfigService.findMatchingConfig(
                request.getSourceCurrency(),
                request.getTargetCurrency(),
                request.getSourceAmount()
        );

        var exchangeRateResult = exchangeRateService.calculateExchangeRate(
                request.getSourceCurrency(),
                request.getTargetCurrency(),
                request.getSourceAmount()
        );

        var feeResult = feeCalculationService.calculateFees(request.getSourceAmount(), exchangeConfig);
        BigDecimal targetAmount = calculateTargetAmount(request.getSourceAmount(), exchangeRateResult, feeResult);

        Payment payment = mapper.toEntity(request);
        payment.setReferenceNumber(ReferenceNumberGenerator.generate());
        payment.setIdempotencyKey(request.getIdempotencyKey());
        payment.setExchangeRateCalculation(exchangeRateResult);
        payment.setFeeCalculation(feeResult);
        payment.setTargetAmount(targetAmount);
        payment.setCreatedBy(adminId != null ? adminId : "system");
        payment.setCreatedByRole(UserRole.ADMIN);
        payment.setEstimatedDeliveryDate(calculateEstimatedDeliveryDate());
        payment.getSender().setReferenceNumber(ReferenceNumberGenerator.generateSenderReference());
        payment.getReceiver().setReferenceNumber(ReferenceNumberGenerator.generateReceiverReference());
        
        encryptSensitive(payment);

        addStatusHistory(payment, PaymentStatus.PENDING_ADMIN_REVIEW, adminId, UserRole.ADMIN, "Payment created");

        Payment saved = repository.save(payment);
        log.info("Payment created with ID: {} and reference: {}", saved.getId(), saved.getReferenceNumber());

        PaymentResponse response = mapper.toResponse(saved);
        decryptAndMaskResponse(response);

        performAutomatedVerification(saved, request);

        return response;
    }

    @Override
    public PaymentResponse validate(String id, StatusUpdateRequest request, String adminId) {
        log.info("Validating payment with ID: {} to status: {}", id, request.getStatus());

        Payment payment = findByIdOrThrow(id);
        
        if (!payment.isSystemVerified()) {
            throw new IllegalArgumentException("Payment must be verified by system before admin verification.");
        }
        
        validateStatusTransition(payment.getStatus(), request.getStatus());

        payment.setStatus(request.getStatus());
        payment.setValidatedBy(adminId != null ? adminId : "system");
        payment.setValidatedByRole(UserRole.ADMIN);

        addStatusHistory(payment, request.getStatus(), adminId, UserRole.ADMIN, request.getNote());

        Payment updated = repository.save(payment);
        log.info("Payment validated with ID: {} to status: {}", updated.getId(), updated.getStatus());

        PaymentResponse response = mapper.toResponse(updated);
        decryptAndMaskResponse(response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAll(PaymentStatus status, LocalDateTime dateFrom, LocalDateTime dateTo,
                                         String senderReference, Pageable pageable) {
        log.info("Fetching payments with filters - status: {}, dateFrom: {}, dateTo: {}, senderReference: {}",
                status, dateFrom, dateTo, senderReference);

        Query query = PaymentSpecification.buildQuery(status, dateFrom, dateTo, senderReference);
        query.with(pageable);

        long total = mongoTemplate.count(query, Payment.class);
        List<Payment> payments = mongoTemplate.find(query, Payment.class);

        Page<Payment> paymentPage = new PageImpl<>(payments, pageable, total);

        return paymentPage.map(payment -> {
            PaymentResponse response = mapper.toResponse(payment);
            decryptAndMaskResponse(response);

            return response;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDetailResponse find(String id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = findByIdOrThrow(id);
        PaymentDetailResponse response = mapper.toDetailResponse(payment);
        decryptAndMaskDetailResponse(response);
        return response;
    }

    @Async
    protected void performAutomatedVerification(Payment payment, PaymentRequest request) {
        log.info("Starting async verification for payment: {}", payment.getId());
        
        try {
            CompletableFuture<PaymentStatus> future = systemVerificationService.performAsyncVerification(request);
            
            future.thenAccept(status -> {
                try {
                    Payment paymentToUpdate = repository.findById(payment.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + payment.getId()));
                    
                    if (status != PaymentStatus.PENDING_ADMIN_REVIEW) {
                        paymentToUpdate.setStatus(status);
                        paymentToUpdate.setSystemVerified(true);
                        addStatusHistory(paymentToUpdate, status, "system", UserRole.SYSTEM_USER, 
                                "System verification completed: " + status);
                        repository.save(paymentToUpdate);
                        log.info("Payment {} updated to status {} after async verification", paymentToUpdate.getId(), status);
                    } else {
                        paymentToUpdate.setSystemVerified(true);
                        repository.save(paymentToUpdate);
                        log.info("Payment {} verified successfully", paymentToUpdate.getId());
                    }
                } catch (Exception ex) {
                    log.error("Error updating payment after verification: {}", payment.getId(), ex);
                }
            }).exceptionally(ex -> {
                log.error("Error during async verification for payment: {}", payment.getId(), ex);
                return null;
            });
        } catch (Exception ex) {
            log.error("Error starting async verification for payment: {}", payment.getId(), ex);
        }
    }

    private void checkIdempotency(String idempotencyKey) {
        if (idempotencyKey != null) {
            repository.findByIdempotencyKey(idempotencyKey).ifPresent(existing -> {
                throw new IllegalArgumentException("Payment with idempotency key already exists: " + idempotencyKey);
            });
        }
    }

    private BigDecimal calculateTargetAmount(BigDecimal sourceAmount, 
                                           com.xuno.payment.payment.model.valueobject.ExchangeRateCalculationResult exchangeRateResult,
                                           com.xuno.payment.payment.model.valueobject.FeeCalculationResult feeResult) {
        BigDecimal convertedAmount = exchangeRateResult.convertAmount(sourceAmount);

        return convertedAmount.subtract(feeResult.getTotalFee());
    }

    private LocalDateTime calculateEstimatedDeliveryDate() {
        return LocalDateTime.now().plusDays(2);
    }

    private void validateStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        if (currentStatus == PaymentStatus.APPROVED && newStatus == PaymentStatus.REJECTED) {
            throw new IllegalArgumentException("Cannot reject an approved payment");
        }
        if (currentStatus == PaymentStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot change status of a delivered payment");
        }
    }

    private void addStatusHistory(Payment payment, PaymentStatus status, String changedBy, UserRole role, String note) {
        StatusHistory history = StatusHistory.builder()
                .status(status)
                .changedBy(changedBy != null ? changedBy : "system")
                .changedByRole(role)
                .changedAt(LocalDateTime.now())
                .note(note)
                .build();
        payment.addStatusHistory(history);
    }

    private Payment findByIdOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
    }

    private void encryptSensitive(Payment payment) {
        if (payment.getSender() != null && payment.getSender().getFundingAccount() != null) {
            var fundingAccount = payment.getSender().getFundingAccount();
            fundingAccount.setAccountNumber(encrypt(fundingAccount.getAccountNumber()));
            fundingAccount.setRoutingNumber(encrypt(fundingAccount.getRoutingNumber()));
        }

        if (payment.getReceiver() != null && payment.getReceiver().getAccount() != null) {
            var account = payment.getReceiver().getAccount();
            account.setAccountNumber(encrypt(account.getAccountNumber()));
        }

    }

    private void decryptAndMaskResponse(PaymentResponse response) {
        if (response.getSender() != null && response.getSender().getFundingAccount() != null) {
            var fundingAccount = response.getSender().getFundingAccount();
            fundingAccount.setAccountNumber(mask(fundingAccount.getAccountNumber()));
            fundingAccount.setRoutingNumber(mask(fundingAccount.getRoutingNumber()));
        }

        if (response.getReceiver() != null && response.getReceiver().getAccount() != null) {
            var account = response.getReceiver().getAccount();
            account.setAccountNumber(mask(account.getAccountNumber()));
        }
    }

    private void decryptAndMaskDetailResponse(PaymentDetailResponse response) {
        decryptAndMaskResponse(response);
    }

    private String encrypt(String value) {
        if (value == null) {
            return null;
        }
        return encryptionService.encrypt(value);
    }

    private String mask(String encryptedValue) {
        if (encryptedValue == null) {
            return null;
        }
        String decrypted = encryptionService.decrypt(encryptedValue);
        if (decrypted == null || decrypted.length() < 4) {
            return decrypted;
        }
        String lastFour = decrypted.substring(decrypted.length() - 4);
        return "****" + lastFour;
    }
}
