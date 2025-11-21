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
import com.xuno.payment.payment.model.valueobject.*;
import com.xuno.payment.payment.repository.PaymentRepository;
import com.xuno.payment.payment.service.EncryptionService;
import com.xuno.payment.payment.service.ExchangeRateService;
import com.xuno.payment.payment.service.FeeCalculationService;
import com.xuno.payment.payment.service.SystemVerificationService;
import com.xuno.payment.payment.util.ReferenceNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests")
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private PaymentMapper mapper;

    @Mock
    private ExchangeConfigService exchangeConfigService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private FeeCalculationService feeCalculationService;

    @Mock
    private SystemVerificationService systemVerificationService;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;
    private Payment payment;
    private PaymentResponse paymentResponse;
    private ExchangeRateConfiguration exchangeConfig;

    @BeforeEach
    void setUp() {
        // Setup PaymentRequest
        SenderFundingAccountInfo senderFundingAccount = SenderFundingAccountInfo.builder()
                .accountNumber("1234567890")
                .routingNumber("987654321")
                .bankCode("BANK001")
                .build();

        SenderInfo senderInfo = SenderInfo.builder()
                .name("John Doe")
                .address("123 Main St")
                .fundingAccount(senderFundingAccount)
                .build();

        ReceiverAccountInfo receiverAccount = ReceiverAccountInfo.builder()
                .accountNumber("9876543210")
                .bankCode("BANK002")
                .swiftCode("SWIFT123")
                .build();

        ReceiverInfo receiverInfo = ReceiverInfo.builder()
                .name("Jane Smith")
                .address("456 Oak Ave")
                .account(receiverAccount)
                .build();

        paymentRequest = PaymentRequest.builder()
                .idempotencyKey("test-key-123")
                .sender(senderInfo)
                .receiver(receiverInfo)
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .sourceCountry("US")
                .destinationCountry("DE")
                .sourceAmount(new BigDecimal("1000.00"))
                .purpose("Payment for services")
                .corridor("US-EU")
                .build();

        // Setup ExchangeRateConfiguration
        exchangeConfig = ExchangeRateConfiguration.builder()
                .id("config-1")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .feeFlat(new BigDecimal("10.00"))
                .feePercent(new BigDecimal("0.04"))
                .build();

        // Setup Payment entity
        ExchangeRateCalculationResult exchangeRateResult = ExchangeRateCalculationResult.builder()
                .exchangeConfigId("config-1")
                .exchangeRate(new BigDecimal("0.95"))
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .appliedAt(LocalDateTime.now())
                .build();

        FeeCalculationResult feeResult = FeeCalculationResult.builder()
                .feeFlat(new BigDecimal("10.00"))
                .feePercent(new BigDecimal("0.04"))
                .flatFeeAmount(new BigDecimal("10.00"))
                .percentFeeAmount(new BigDecimal("40.00"))
                .totalFee(new BigDecimal("50.00"))
                .calculatedAt(LocalDateTime.now())
                .build();

        payment = Payment.builder()
                .id("payment-1")
                .referenceNumber("TXN-12345")
                .idempotencyKey("test-key-123")
                .sender(senderInfo)
                .receiver(receiverInfo)
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .sourceCountry("US")
                .destinationCountry("DE")
                .sourceAmount(new BigDecimal("1000.00"))
                .targetAmount(new BigDecimal("900.00"))
                .purpose("Payment for services")
                .corridor("US-EU")
                .status(PaymentStatus.PENDING_ADMIN_REVIEW)
                .exchangeRateCalculation(exchangeRateResult)
                .feeCalculation(feeResult)
                .createdBy("admin-123")
                .createdByRole(UserRole.ADMIN)
                .systemVerified(false)
                .statusHistory(new ArrayList<>())
                .build();

        // Setup PaymentResponse
        paymentResponse = PaymentResponse.builder()
                .id("payment-1")
                .referenceNumber("TXN-12345")
                .sender(senderInfo)
                .receiver(receiverInfo)
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .sourceAmount(new BigDecimal("1000.00"))
                .targetAmount(new BigDecimal("900.00"))
                .status(PaymentStatus.PENDING_ADMIN_REVIEW)
                .build();
    }

    @Test
    @DisplayName("Should create payment successfully")
    void testCreatePayment_Success() {
        // Given
        String adminId = "admin-123";
        ExchangeRateCalculationResult exchangeRateResult = ExchangeRateCalculationResult.builder()
                .exchangeConfigId("config-1")
                .exchangeRate(new BigDecimal("0.95"))
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .appliedAt(LocalDateTime.now())
                .build();

        FeeCalculationResult feeResult = FeeCalculationResult.builder()
                .feeFlat(new BigDecimal("10.00"))
                .feePercent(new BigDecimal("0.04"))
                .flatFeeAmount(new BigDecimal("10.00"))
                .percentFeeAmount(new BigDecimal("40.00"))
                .totalFee(new BigDecimal("50.00"))
                .calculatedAt(LocalDateTime.now())
                .build();

        when(repository.findByIdempotencyKey("test-key-123")).thenReturn(Optional.empty());
        doNothing().when(systemVerificationService).performInitialVerification(paymentRequest);
        when(systemVerificationService.performAsyncVerification(any(PaymentRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(PaymentStatus.PENDING_ADMIN_REVIEW));
        when(exchangeConfigService.findMatchingConfig("USD", "EUR", new BigDecimal("1000.00")))
                .thenReturn(exchangeConfig);
        when(exchangeRateService.calculateExchangeRate("USD", "EUR", new BigDecimal("1000.00")))
                .thenReturn(exchangeRateResult);
        when(feeCalculationService.calculateFees(new BigDecimal("1000.00"), exchangeConfig))
                .thenReturn(feeResult);
        when(mapper.toEntity(paymentRequest)).thenReturn(payment);
        when(repository.save(any(Payment.class))).thenReturn(payment);
        when(mapper.toResponse(payment)).thenReturn(paymentResponse);
        when(encryptionService.encrypt(anyString())).thenAnswer(invocation -> "encrypted-" + invocation.getArgument(0));
        when(encryptionService.decrypt(anyString())).thenAnswer(invocation -> {
            String encrypted = invocation.getArgument(0);
            return encrypted.replace("encrypted-", "");
        });

        try (MockedStatic<ReferenceNumberGenerator> mockedGenerator = mockStatic(ReferenceNumberGenerator.class)) {
            mockedGenerator.when(ReferenceNumberGenerator::generate).thenReturn("TXN-12345");
            mockedGenerator.when(ReferenceNumberGenerator::generateSenderReference).thenReturn("SND-123");
            mockedGenerator.when(ReferenceNumberGenerator::generateReceiverReference).thenReturn("RCV-456");

            // When
            PaymentResponse result = paymentService.create(paymentRequest, adminId);

            // Then
            assertNotNull(result);
            assertEquals("payment-1", result.getId());
            verify(repository).findByIdempotencyKey("test-key-123");
            verify(systemVerificationService).performInitialVerification(paymentRequest);
            verify(exchangeConfigService).findMatchingConfig("USD", "EUR", new BigDecimal("1000.00"));
            verify(exchangeRateService).calculateExchangeRate("USD", "EUR", new BigDecimal("1000.00"));
            verify(feeCalculationService).calculateFees(new BigDecimal("1000.00"), exchangeConfig);
            verify(repository).save(any(Payment.class));
            verify(mapper).toResponse(payment);
        }
    }

    @Test
    @DisplayName("Should throw exception when idempotency key already exists")
    void testCreatePayment_DuplicateIdempotencyKey() {
        // Given
        Payment existingPayment = Payment.builder()
                .id("existing-payment")
                .idempotencyKey("test-key-123")
                .build();

        when(repository.findByIdempotencyKey("test-key-123"))
                .thenReturn(Optional.of(existingPayment));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.create(paymentRequest, "admin-123");
        });

        assertTrue(exception.getMessage().contains("idempotency key already exists"));
        verify(repository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should validate payment successfully")
    void testValidatePayment_Success() {
        // Given
        String paymentId = "payment-1";
        String adminId = "admin-123";
        StatusUpdateRequest statusUpdateRequest = StatusUpdateRequest.builder()
                .status(PaymentStatus.APPROVED)
                .note("Approved by admin")
                .build();

        payment.setSystemVerified(true);
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .id("payment-1")
                .status(PaymentStatus.APPROVED)
                .build();

        when(repository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(repository.save(any(Payment.class))).thenReturn(payment);
        when(mapper.toResponse(any(Payment.class))).thenReturn(expectedResponse);

        // When
        PaymentResponse result = paymentService.validate(paymentId, statusUpdateRequest, adminId);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        verify(repository).findById(paymentId);
        verify(repository).save(any(Payment.class));
        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        assertEquals(adminId, payment.getValidatedBy());
    }

    @Test
    @DisplayName("Should throw exception when payment not system verified")
    void testValidatePayment_NotSystemVerified() {
        // Given
        String paymentId = "payment-1";
        StatusUpdateRequest statusUpdateRequest = StatusUpdateRequest.builder()
                .status(PaymentStatus.APPROVED)
                .build();

        payment.setSystemVerified(false);

        when(repository.findById(paymentId)).thenReturn(Optional.of(payment));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.validate(paymentId, statusUpdateRequest, "admin-123");
        });

        assertTrue(exception.getMessage().contains("must be verified by system"));
        verify(repository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when invalid status transition")
    void testValidatePayment_InvalidStatusTransition() {
        // Given
        String paymentId = "payment-1";
        StatusUpdateRequest statusUpdateRequest = StatusUpdateRequest.builder()
                .status(PaymentStatus.REJECTED)
                .build();

        payment.setSystemVerified(true);
        payment.setStatus(PaymentStatus.APPROVED);

        when(repository.findById(paymentId)).thenReturn(Optional.of(payment));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.validate(paymentId, statusUpdateRequest, "admin-123");
        });

        assertTrue(exception.getMessage().contains("Cannot reject an approved payment"));
        verify(repository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should find payment by ID successfully")
    void testFindPayment_Success() {
        // Given
        String paymentId = "payment-1";
        PaymentDetailResponse detailResponse = PaymentDetailResponse.builder()
                .id("payment-1")
                .status(PaymentStatus.PENDING_ADMIN_REVIEW)
                .statusHistory(new ArrayList<>())
                .sender(SenderInfo.builder()
                        .fundingAccount(SenderFundingAccountInfo.builder()
                                .accountNumber("encrypted-1234567890")
                                .routingNumber("encrypted-987654321")
                                .build())
                        .build())
                .receiver(ReceiverInfo.builder()
                        .account(ReceiverAccountInfo.builder()
                                .accountNumber("encrypted-9876543210")
                                .build())
                        .build())
                .build();

        when(repository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(mapper.toDetailResponse(payment)).thenReturn(detailResponse);
        when(encryptionService.decrypt(anyString())).thenAnswer(invocation -> {
            String encrypted = invocation.getArgument(0);
            return encrypted.replace("encrypted-", "");
        });

        // When
        PaymentDetailResponse result = paymentService.find(paymentId);

        // Then
        assertNotNull(result);
        assertEquals("payment-1", result.getId());
        verify(repository).findById(paymentId);
        verify(mapper).toDetailResponse(payment);
    }

    @Test
    @DisplayName("Should throw exception when payment not found")
    void testFindPayment_NotFound() {
        // Given
        String paymentId = "non-existent";

        when(repository.findById(paymentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.find(paymentId);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(repository).findById(paymentId);
    }

    @Test
    @DisplayName("Should find all payments with filters")
    void testFindAllPayments_WithFilters() {
        // Given
        PaymentStatus status = PaymentStatus.PENDING_ADMIN_REVIEW;
        LocalDateTime dateFrom = LocalDateTime.now().minusDays(7);
        LocalDateTime dateTo = LocalDateTime.now();
        String senderReference = "SND-123";
        Pageable pageable = PageRequest.of(0, 20);

        // Setup paymentResponse with encrypted data to simulate real scenario
        PaymentResponse responseWithEncryptedData = PaymentResponse.builder()
                .id("payment-1")
                .sender(SenderInfo.builder()
                        .fundingAccount(SenderFundingAccountInfo.builder()
                                .accountNumber("encrypted-1234567890")
                                .routingNumber("encrypted-987654321")
                                .build())
                        .build())
                .receiver(ReceiverInfo.builder()
                        .account(ReceiverAccountInfo.builder()
                                .accountNumber("encrypted-9876543210")
                                .build())
                        .build())
                .build();

        when(mongoTemplate.count(any(Query.class), eq(Payment.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Payment.class))).thenReturn(List.of(payment));
        when(mapper.toResponse(any(Payment.class))).thenReturn(responseWithEncryptedData);
        when(encryptionService.decrypt(anyString())).thenAnswer(invocation -> {
            String encrypted = invocation.getArgument(0);
            return encrypted.replace("encrypted-", "");
        });

        // When
        Page<PaymentResponse> result = paymentService.findAll(status, dateFrom, dateTo, senderReference, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(mongoTemplate).count(any(Query.class), eq(Payment.class));
        verify(mongoTemplate).find(any(Query.class), eq(Payment.class));
    }
}

