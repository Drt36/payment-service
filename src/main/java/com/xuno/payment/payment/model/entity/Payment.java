package com.xuno.payment.payment.model.entity;

import com.xuno.payment.payment.model.enums.PaymentStatus;
import com.xuno.payment.payment.model.enums.UserRole;
import com.xuno.payment.payment.model.valueobject.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "payments")
@CompoundIndex(name = "status_created_idx", def = "{'status': 1, 'createdAt': -1}")
@CompoundIndex(name = "sender_created_idx", def = "{'sender.referenceNumber': 1, 'createdAt': -1}")
@CompoundIndex(name = "reference_number_idx", def = "{'referenceNumber': 1}")
@CompoundIndex(name = "idempotency_key_idx", def = "{'idempotencyKey': 1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    private String id;

    private String referenceNumber;
    private String idempotencyKey;

    private SenderInfo sender;
    private ReceiverInfo receiver;

    private String sourceCurrency;
    private String targetCurrency;
    private String sourceCountry;
    private String destinationCountry;
    private String corridor;

    private BigDecimal sourceAmount;
    private BigDecimal targetAmount;

    private String purpose;

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING_ADMIN_REVIEW;

    @Builder.Default
    private List<StatusHistory> statusHistory = new ArrayList<>();

    private ExchangeRateCalculationResult exchangeRateCalculation;
    private FeeCalculationResult feeCalculation;

    private String createdBy;
    private UserRole createdByRole;

    private String validatedBy;
    private UserRole validatedByRole;

    @Builder.Default
    private boolean systemVerified = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime estimatedDeliveryDate;

    @Builder.Default
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public void addStatusHistory(StatusHistory history) {
        if (this.statusHistory == null) {
            this.statusHistory = new ArrayList<>();
        }
        this.statusHistory.add(history);
    }
}
