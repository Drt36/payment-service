package com.xuno.payment.payment.service;

import com.xuno.payment.payment.model.dto.PaymentRequest;
import com.xuno.payment.payment.model.enums.PaymentStatus;

import java.util.concurrent.CompletableFuture;

public interface SystemVerificationService {

    void performInitialVerification(PaymentRequest request);

    CompletableFuture<PaymentStatus> performAsyncVerification(PaymentRequest request);
}
