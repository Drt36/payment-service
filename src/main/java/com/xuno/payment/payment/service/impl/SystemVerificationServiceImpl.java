package com.xuno.payment.payment.service.impl;

import com.xuno.payment.payment.model.dto.PaymentRequest;
import com.xuno.payment.payment.model.enums.PaymentStatus;
import com.xuno.payment.payment.service.SystemVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemVerificationServiceImpl implements SystemVerificationService {

    private static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("100.00");
    private static final BigDecimal MAX_AMOUNT_LIMIT = new BigDecimal("1000000.00");
    private static final String MISINFORMATION_SENDER_NAME = "Test Sender";
    private static final String MISINFORMATION_RECEIVER_NAME = "Test Receiver";

    @Override
    public void performInitialVerification(PaymentRequest request) {
        log.debug("Performing initial verification for payment request");
        
        validateBasicRules(request);
    }

    @Override
    @Async
    public CompletableFuture<PaymentStatus> performAsyncVerification(PaymentRequest request) {
        log.info("Starting async system verification for payment");
        
        PaymentStatus status = PaymentStatus.PENDING_ADMIN_REVIEW;
        
        if (checkLowBalance(request)) {
            log.warn("Low balance detected for sender: {}", request.getSender().getName());
            status = PaymentStatus.LOW_BALANCE;
        } else if (checkSenderMisinformation(request)) {
            log.warn("Sender misinformation detected: {}", request.getSender().getName());
            status = PaymentStatus.MISINFORMATION_SENDER;
        } else if (checkReceiverMisinformation(request)) {
            log.warn("Receiver misinformation detected: {}", request.getReceiver().getName());
            status = PaymentStatus.MISINFORMATION_RECEIVER;
        } else if (checkAmountLimit(request)) {
            log.warn("Amount limit exceeded: {}", request.getSourceAmount());
            status = PaymentStatus.REJECTED;
        } else if (checkCurrencyRestrictions(request)) {
            log.warn("Currency restrictions violated");
            status = PaymentStatus.REJECTED;
        }
        
        log.info("Async verification completed with status: {}", status);
        return CompletableFuture.completedFuture(status);
    }

    private void validateBasicRules(PaymentRequest request) {
        if (request.getSender() == null || request.getReceiver() == null) {
            throw new IllegalArgumentException("Sender and receiver information are required");
        }
        if (request.getSender().getFundingAccount() == null || 
            request.getSender().getFundingAccount().getAccountNumber() == null) {
            throw new IllegalArgumentException("Sender account information is required");
        }
        if (request.getReceiver().getAccount() == null || 
            request.getReceiver().getAccount().getAccountNumber() == null) {
            throw new IllegalArgumentException("Receiver account information is required");
        }
        if (request.getSourceCurrency().equals(request.getTargetCurrency())) {
            throw new IllegalArgumentException("Source and target currencies must be different");
        }
    }

    private boolean checkLowBalance(PaymentRequest request) {
        BigDecimal amount = request.getSourceAmount();
        return amount.compareTo(LOW_BALANCE_THRESHOLD) < 0;
    }

    private boolean checkSenderMisinformation(PaymentRequest request) {
        String senderName = request.getSender().getName();
        return senderName != null && senderName.equalsIgnoreCase(MISINFORMATION_SENDER_NAME);
    }

    private boolean checkReceiverMisinformation(PaymentRequest request) {
        String receiverName = request.getReceiver().getName();
        return receiverName != null && receiverName.equalsIgnoreCase(MISINFORMATION_RECEIVER_NAME);
    }

    private boolean checkAmountLimit(PaymentRequest request) {
        return request.getSourceAmount().compareTo(MAX_AMOUNT_LIMIT) > 0;
    }

    private boolean checkCurrencyRestrictions(PaymentRequest request) {
        String sourceCountry = request.getSourceCountry();
        String destCountry = request.getDestinationCountry();
        
        return sourceCountry != null && destCountry != null && 
               sourceCountry.equals(destCountry) && 
               !request.getSourceCurrency().equals(request.getTargetCurrency());
    }
}

