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
public class ExchangeRateCalculationResult {
    private String exchangeConfigId;
    private BigDecimal exchangeRate;
    private String sourceCurrency;
    private String targetCurrency;
    private LocalDateTime appliedAt;

    public BigDecimal convertAmount(BigDecimal sourceAmount) {
        return sourceAmount.multiply(exchangeRate);
    }
}

