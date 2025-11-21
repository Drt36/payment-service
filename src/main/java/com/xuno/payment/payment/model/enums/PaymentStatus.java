package com.xuno.payment.payment.model.enums;

public enum PaymentStatus {
    INITIATED,
    CREATED,
    PENDING_ADMIN_REVIEW,
    APPROVED,
    DELIVERED,
    REJECTED,
    LOW_BALANCE,
    MISINFORMATION_SENDER,
    MISINFORMATION_RECEIVER
}

