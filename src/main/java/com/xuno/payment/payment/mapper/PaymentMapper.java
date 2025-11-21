package com.xuno.payment.payment.mapper;

import com.xuno.payment.payment.model.dto.PaymentDetailResponse;
import com.xuno.payment.payment.model.dto.PaymentRequest;
import com.xuno.payment.payment.model.dto.PaymentResponse;
import com.xuno.payment.payment.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    Payment toEntity(PaymentRequest request);

    PaymentResponse toResponse(Payment entity);

    PaymentDetailResponse toDetailResponse(Payment entity);
}
