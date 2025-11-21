package com.xuno.payment.exchangeconfig.service;

import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigRequest;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigResponse;
import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeConfigService {

    ExchangeConfigResponse create(ExchangeConfigRequest request);

    List<ExchangeConfigResponse> findAll();

    ExchangeConfigResponse find(String id);

    ExchangeConfigResponse update(String id, ExchangeConfigRequest request);

    void delete(String id);

    ExchangeRateConfiguration findMatchingConfig(String sourceCurrency, String targetCurrency, BigDecimal amount);
}
