package com.xuno.payment.payment.service.impl;

import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import com.xuno.payment.exchangeconfig.service.ExchangeConfigService;
import com.xuno.payment.payment.model.valueobject.ExchangeRateCalculationResult;
import com.xuno.payment.payment.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeConfigService exchangeConfigService;

    @Override
    public ExchangeRateCalculationResult calculateExchangeRate(
            String sourceCurrency,
            String targetCurrency,
            BigDecimal sourceAmount) {
        
        log.debug("Calculating exchange rate for {} to {} with amount {}", 
                sourceCurrency, targetCurrency, sourceAmount);
        
        ExchangeRateConfiguration config = exchangeConfigService.findMatchingConfig(
                sourceCurrency, targetCurrency, sourceAmount);
        
        return ExchangeRateCalculationResult.builder()
                .exchangeConfigId(config.getId())
                .exchangeRate(config.getFxRate())
                .sourceCurrency(config.getSourceCurrency())
                .targetCurrency(config.getTargetCurrency())
                .appliedAt(LocalDateTime.now())
                .build();
    }
}

