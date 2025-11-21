package com.xuno.payment.payment.service;

import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import com.xuno.payment.payment.model.valueobject.FeeCalculationResult;

import java.math.BigDecimal;

public interface FeeCalculationService {

    FeeCalculationResult calculateFees(BigDecimal sourceAmount, ExchangeRateConfiguration config);
}
