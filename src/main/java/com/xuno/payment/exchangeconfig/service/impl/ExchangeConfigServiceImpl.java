package com.xuno.payment.exchangeconfig.service.impl;

import com.xuno.payment.common.exception.ResourceNotFoundException;
import com.xuno.payment.exchangeconfig.mapper.ExchangeConfigMapper;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigRequest;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigResponse;
import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import com.xuno.payment.exchangeconfig.repository.ExchangeConfigRepository;
import com.xuno.payment.exchangeconfig.service.ExchangeConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExchangeConfigServiceImpl implements ExchangeConfigService {

    private final ExchangeConfigRepository repository;
    private final ExchangeConfigMapper mapper;

    @Override
    public ExchangeConfigResponse create(ExchangeConfigRequest request) {
        log.info("Creating exchange configuration: {} to {}",
                request.getSourceCurrency(), request.getTargetCurrency());

        validateRequest(request);
        ExchangeRateConfiguration entity = mapper.toEntity(request);
        ExchangeRateConfiguration saved = repository.save(entity);
        log.info("Exchange configuration created with ID: {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExchangeConfigResponse> findAll() {
        log.info("Fetching all exchange configurations");

        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExchangeConfigResponse find(String id) {
        log.info("Fetching exchange configuration with ID: {}", id);
        ExchangeRateConfiguration entity = findByIdOrThrow(id);

        return mapper.toResponse(entity);
    }

    @Override
    public ExchangeConfigResponse update(String id, ExchangeConfigRequest request) {
        log.info("Updating exchange configuration with ID: {}", id);

        validateRequest(request);
        ExchangeRateConfiguration entity = findByIdOrThrow(id);
        mapper.updateRequestToEntity(entity, request);
        ExchangeRateConfiguration updated = repository.save(entity);
        log.info("Exchange configuration updated with ID: {}", updated.getId());

        return mapper.toResponse(updated);
    }

    @Override
    public void delete(String id) {
        log.info("Deleting exchange configuration with ID: {}", id);

        ExchangeRateConfiguration entity = findByIdOrThrow(id);
        entity.markAsDeleted();
        repository.save(entity);

        log.info("Exchange configuration soft deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExchangeRateConfiguration findMatchingConfig(
            String sourceCurrency,
            String targetCurrency,
            BigDecimal amount) {
        log.info("Finding matching exchange config for {} to {} with amount {}", 
                sourceCurrency, targetCurrency, amount);
        
        return repository.findMatchingConfig(sourceCurrency, targetCurrency, amount)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No exchange configuration found for %s to %s with amount %s",
                                sourceCurrency, targetCurrency, amount)));
    }

    private ExchangeRateConfiguration findByIdOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exchange configuration not found with ID: " + id));
    }

    private void validateRequest(ExchangeConfigRequest request) {
        if (request.getMinAmount().compareTo(request.getMaxAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Minimum amount cannot be greater than maximum amount");
        }
    }

}
