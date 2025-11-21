package com.xuno.payment.payment.service;

import com.xuno.payment.payment.model.dto.PaymentDetailResponse;
import com.xuno.payment.payment.model.dto.PaymentRequest;
import com.xuno.payment.payment.model.dto.PaymentResponse;
import com.xuno.payment.payment.model.dto.StatusUpdateRequest;
import com.xuno.payment.payment.model.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request, String adminId);

    PaymentResponse validate(String id, StatusUpdateRequest request, String adminId);

    Page<PaymentResponse> findAll(PaymentStatus status, LocalDateTime dateFrom, LocalDateTime dateTo, 
                                  String senderReference, Pageable pageable);

    PaymentDetailResponse find(String id);
}
