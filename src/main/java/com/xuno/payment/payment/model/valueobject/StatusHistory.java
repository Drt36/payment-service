package com.xuno.payment.payment.model.valueobject;

import com.xuno.payment.payment.model.enums.PaymentStatus;
import com.xuno.payment.payment.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    private PaymentStatus status;
    private String changedBy;
    private UserRole changedByRole;
    private LocalDateTime changedAt;
    private String note;
}

