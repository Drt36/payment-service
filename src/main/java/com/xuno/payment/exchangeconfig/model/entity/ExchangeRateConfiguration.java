package com.xuno.payment.exchangeconfig.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "exchange_configs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateConfiguration {

    @Id
    private String id;

    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal fxRate;
    private BigDecimal feeFlat;
    private BigDecimal feePercent;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public void markAsDeleted() {
        this.deleted = true;
        if (this.deletedAt == null) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}
