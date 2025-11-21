package com.xuno.payment.payment.service.impl;

import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import com.xuno.payment.payment.model.valueobject.FeeCalculationResult;
import com.xuno.payment.payment.service.FeeCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeCalculationServiceImpl implements FeeCalculationService {

    @Override
    public FeeCalculationResult calculateFees(BigDecimal sourceAmount, ExchangeRateConfiguration config) {
        log.debug("Calculating fees for amount {} using config {}", sourceAmount, config.getId());
        
        BigDecimal flatFee = config.getFeeFlat() != null ? config.getFeeFlat() : BigDecimal.ZERO;
        BigDecimal percentFee = config.getFeePercent() != null ? config.getFeePercent() : BigDecimal.ZERO;
        BigDecimal percentFeeAmount = sourceAmount.multiply(percentFee)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal totalFee = flatFee.add(percentFeeAmount);
        
        return FeeCalculationResult.builder()
                .feeFlat(flatFee)
                .feePercent(percentFee)
                .flatFeeAmount(flatFee)
                .percentFeeAmount(percentFeeAmount)
                .totalFee(totalFee)
                .calculatedAt(LocalDateTime.now())
                .build();
    }
}

