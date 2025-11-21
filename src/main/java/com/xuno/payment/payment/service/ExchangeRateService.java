package com.xuno.payment.payment.service;

import com.xuno.payment.payment.model.valueobject.ExchangeRateCalculationResult;

import java.math.BigDecimal;

public interface ExchangeRateService {

    ExchangeRateCalculationResult calculateExchangeRate(
            String sourceCurrency,
            String targetCurrency,
            BigDecimal sourceAmount);
}
