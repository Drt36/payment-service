package com.xuno.payment.payment.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeCalculationResult {
    private BigDecimal feeFlat;
    private BigDecimal feePercent;
    private BigDecimal flatFeeAmount;
    private BigDecimal percentFeeAmount;
    private BigDecimal totalFee;
    private LocalDateTime calculatedAt;
}

