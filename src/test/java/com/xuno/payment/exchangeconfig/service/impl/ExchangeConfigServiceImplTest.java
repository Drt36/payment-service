package com.xuno.payment.exchangeconfig.service.impl;

import com.xuno.payment.common.exception.ResourceNotFoundException;
import com.xuno.payment.exchangeconfig.mapper.ExchangeConfigMapper;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigRequest;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigResponse;
import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import com.xuno.payment.exchangeconfig.repository.ExchangeConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeConfigService Tests")
class ExchangeConfigServiceImplTest {

    @Mock
    private ExchangeConfigRepository repository;

    @Mock
    private ExchangeConfigMapper mapper;

    @InjectMocks
    private ExchangeConfigServiceImpl exchangeConfigService;

    private ExchangeConfigRequest request;
    private ExchangeRateConfiguration entity;
    private ExchangeConfigResponse response;

    @BeforeEach
    void setUp() {
        request = ExchangeConfigRequest.builder()
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .feeFlat(new BigDecimal("10.00"))
                .feePercent(new BigDecimal("0.04"))
                .build();

        entity = ExchangeRateConfiguration.builder()
                .id("config-1")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .feeFlat(new BigDecimal("10.00"))
                .feePercent(new BigDecimal("0.04"))
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        response = ExchangeConfigResponse.builder()
                .id("config-1")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .feeFlat(new BigDecimal("10.00"))
                .feePercent(new BigDecimal("0.04"))
                .build();
    }

    @Test
    @DisplayName("Should create exchange configuration successfully")
    void testCreate_Success() {
        // Given
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        ExchangeConfigResponse result = exchangeConfigService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("config-1", result.getId());
        assertEquals("USD", result.getSourceCurrency());
        assertEquals("EUR", result.getTargetCurrency());
        verify(mapper).toEntity(request);
        verify(repository).save(entity);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("Should throw exception when min amount is greater than max amount")
    void testCreate_InvalidAmountRange() {
        // Given
        ExchangeConfigRequest invalidRequest = ExchangeConfigRequest.builder()
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("1000.00"))
                .maxAmount(new BigDecimal("100.00")) // Invalid: min > max
                .fxRate(new BigDecimal("0.95"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exchangeConfigService.create(invalidRequest);
        });

        assertTrue(exception.getMessage().contains("Minimum amount cannot be greater than maximum amount"));
        verify(repository, never()).save(any(ExchangeRateConfiguration.class));
    }

    @Test
    @DisplayName("Should create exchange configuration without optional fees")
    void testCreate_WithoutOptionalFees() {
        // Given
        ExchangeConfigRequest requestWithoutFees = ExchangeConfigRequest.builder()
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .build();

        ExchangeRateConfiguration entityWithoutFees = ExchangeRateConfiguration.builder()
                .id("config-2")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .build();

        ExchangeConfigResponse responseWithoutFees = ExchangeConfigResponse.builder()
                .id("config-2")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .build();

        when(mapper.toEntity(requestWithoutFees)).thenReturn(entityWithoutFees);
        when(repository.save(entityWithoutFees)).thenReturn(entityWithoutFees);
        when(mapper.toResponse(entityWithoutFees)).thenReturn(responseWithoutFees);

        // When
        ExchangeConfigResponse result = exchangeConfigService.create(requestWithoutFees);

        // Then
        assertNotNull(result);
        assertEquals("config-2", result.getId());
        assertNull(result.getFeeFlat());
        assertNull(result.getFeePercent());
        verify(repository).save(entityWithoutFees);
    }

    @Test
    @DisplayName("Should find all exchange configurations successfully")
    void testFindAll_Success() {
        // Given
        ExchangeRateConfiguration entity2 = ExchangeRateConfiguration.builder()
                .id("config-2")
                .sourceCurrency("GBP")
                .targetCurrency("USD")
                .minAmount(new BigDecimal("50.00"))
                .maxAmount(new BigDecimal("50000.00"))
                .fxRate(new BigDecimal("1.25"))
                .build();

        ExchangeConfigResponse response2 = ExchangeConfigResponse.builder()
                .id("config-2")
                .sourceCurrency("GBP")
                .targetCurrency("USD")
                .minAmount(new BigDecimal("50.00"))
                .maxAmount(new BigDecimal("50000.00"))
                .fxRate(new BigDecimal("1.25"))
                .build();

        List<ExchangeRateConfiguration> entities = List.of(entity, entity2);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toResponse(entity)).thenReturn(response);
        when(mapper.toResponse(entity2)).thenReturn(response2);

        // When
        List<ExchangeConfigResponse> results = exchangeConfigService.findAll();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("config-1", results.get(0).getId());
        assertEquals("config-2", results.get(1).getId());
        verify(repository).findAll();
        verify(mapper, times(2)).toResponse(any(ExchangeRateConfiguration.class));
    }

    @Test
    @DisplayName("Should return empty list when no configurations exist")
    void testFindAll_EmptyList() {
        // Given
        when(repository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<ExchangeConfigResponse> results = exchangeConfigService.findAll();

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should find exchange configuration by ID successfully")
    void testFind_Success() {
        // Given
        String configId = "config-1";

        when(repository.findById(configId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        ExchangeConfigResponse result = exchangeConfigService.find(configId);

        // Then
        assertNotNull(result);
        assertEquals("config-1", result.getId());
        assertEquals("USD", result.getSourceCurrency());
        verify(repository).findById(configId);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("Should throw exception when configuration not found")
    void testFind_NotFound() {
        // Given
        String configId = "non-existent";

        when(repository.findById(configId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            exchangeConfigService.find(configId);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(repository).findById(configId);
        verify(mapper, never()).toResponse(any(ExchangeRateConfiguration.class));
    }

    @Test
    @DisplayName("Should update exchange configuration successfully")
    void testUpdate_Success() {
        // Given
        String configId = "config-1";
        ExchangeConfigRequest updateRequest = ExchangeConfigRequest.builder()
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("200.00"))
                .maxAmount(new BigDecimal("200000.00"))
                .fxRate(new BigDecimal("0.96"))
                .feeFlat(new BigDecimal("15.00"))
                .feePercent(new BigDecimal("0.05"))
                .build();

        ExchangeConfigResponse updatedResponse = ExchangeConfigResponse.builder()
                .id("config-1")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("200.00"))
                .maxAmount(new BigDecimal("200000.00"))
                .fxRate(new BigDecimal("0.96"))
                .feeFlat(new BigDecimal("15.00"))
                .feePercent(new BigDecimal("0.05"))
                .build();

        when(repository.findById(configId)).thenReturn(Optional.of(entity));
        doNothing().when(mapper).updateRequestToEntity(entity, updateRequest);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(updatedResponse);

        // When
        ExchangeConfigResponse result = exchangeConfigService.update(configId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals("config-1", result.getId());
        assertEquals(new BigDecimal("0.96"), result.getFxRate());
        assertEquals(new BigDecimal("15.00"), result.getFeeFlat());
        verify(repository).findById(configId);
        verify(mapper).updateRequestToEntity(entity, updateRequest);
        verify(repository).save(entity);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent configuration")
    void testUpdate_NotFound() {
        // Given
        String configId = "non-existent";

        when(repository.findById(configId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            exchangeConfigService.update(configId, request);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(repository).findById(configId);
        verify(repository, never()).save(any(ExchangeRateConfiguration.class));
    }

    @Test
    @DisplayName("Should throw exception when update request has invalid amount range")
    void testUpdate_InvalidAmountRange() {
        // Given
        String configId = "config-1";
        ExchangeConfigRequest invalidRequest = ExchangeConfigRequest.builder()
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("1000.00"))
                .maxAmount(new BigDecimal("100.00")) // Invalid: min > max
                .fxRate(new BigDecimal("0.95"))
                .build();

        // When & Then
        // Validation happens before repository call, so no need to stub repository.findById
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exchangeConfigService.update(configId, invalidRequest);
        });

        assertTrue(exception.getMessage().contains("Minimum amount cannot be greater than maximum amount"));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(ExchangeRateConfiguration.class));
    }

    @Test
    @DisplayName("Should delete exchange configuration successfully (soft delete)")
    void testDelete_Success() {
        // Given
        String configId = "config-1";

        when(repository.findById(configId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        // When
        exchangeConfigService.delete(configId);

        // Then
        assertTrue(entity.isDeleted());
        assertNotNull(entity.getDeletedAt());
        verify(repository).findById(configId);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent configuration")
    void testDelete_NotFound() {
        // Given
        String configId = "non-existent";

        when(repository.findById(configId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            exchangeConfigService.delete(configId);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(repository).findById(configId);
        verify(repository, never()).save(any(ExchangeRateConfiguration.class));
    }

    @Test
    @DisplayName("Should find matching configuration successfully")
    void testFindMatchingConfig_Success() {
        // Given
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal amount = new BigDecimal("1000.00");

        ExchangeRateConfiguration matchingConfig = ExchangeRateConfiguration.builder()
                .id("config-1")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findMatchingConfig(sourceCurrency, targetCurrency, amount))
                .thenReturn(List.of(matchingConfig));

        // When
        ExchangeRateConfiguration result = exchangeConfigService.findMatchingConfig(
                sourceCurrency, targetCurrency, amount);

        // Then
        assertNotNull(result);
        assertEquals("config-1", result.getId());
        assertEquals("USD", result.getSourceCurrency());
        assertEquals("EUR", result.getTargetCurrency());
        verify(repository).findMatchingConfig(sourceCurrency, targetCurrency, amount);
    }

    @Test
    @DisplayName("Should return latest configuration when multiple matches exist")
    void testFindMatchingConfig_MultipleMatches_ReturnsLatest() {
        // Given
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal amount = new BigDecimal("1000.00");

        ExchangeRateConfiguration olderConfig = ExchangeRateConfiguration.builder()
                .id("config-1")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.94"))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        ExchangeRateConfiguration newerConfig = ExchangeRateConfiguration.builder()
                .id("config-2")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(new BigDecimal("100.00"))
                .maxAmount(new BigDecimal("100000.00"))
                .fxRate(new BigDecimal("0.95"))
                .createdAt(LocalDateTime.now())
                .build();

        // Repository returns sorted by createdAt descending (newest first)
        when(repository.findMatchingConfig(sourceCurrency, targetCurrency, amount))
                .thenReturn(List.of(newerConfig, olderConfig));

        // When
        ExchangeRateConfiguration result = exchangeConfigService.findMatchingConfig(
                sourceCurrency, targetCurrency, amount);

        // Then
        assertNotNull(result);
        assertEquals("config-2", result.getId()); // Should return the latest (first in sorted list)
        assertEquals(new BigDecimal("0.95"), result.getFxRate());
        verify(repository).findMatchingConfig(sourceCurrency, targetCurrency, amount);
    }

    @Test
    @DisplayName("Should throw exception when no matching configuration found")
    void testFindMatchingConfig_NotFound() {
        // Given
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal amount = new BigDecimal("1000.00");

        when(repository.findMatchingConfig(sourceCurrency, targetCurrency, amount))
                .thenReturn(new ArrayList<>());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            exchangeConfigService.findMatchingConfig(sourceCurrency, targetCurrency, amount);
        });

        assertTrue(exception.getMessage().contains("No exchange configuration found"));
        verify(repository).findMatchingConfig(sourceCurrency, targetCurrency, amount);
    }

    @Test
    @DisplayName("Should throw exception when amount is below minimum")
    void testFindMatchingConfig_AmountBelowMinimum() {
        // Given
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal amount = new BigDecimal("50.00"); // Below minAmount of 100.00

        when(repository.findMatchingConfig(sourceCurrency, targetCurrency, amount))
                .thenReturn(new ArrayList<>());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            exchangeConfigService.findMatchingConfig(sourceCurrency, targetCurrency, amount);
        });

        assertTrue(exception.getMessage().contains("No exchange configuration found"));
        verify(repository).findMatchingConfig(sourceCurrency, targetCurrency, amount);
    }

    @Test
    @DisplayName("Should throw exception when amount is above maximum")
    void testFindMatchingConfig_AmountAboveMaximum() {
        // Given
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal amount = new BigDecimal("200000.00"); // Above maxAmount of 100000.00

        when(repository.findMatchingConfig(sourceCurrency, targetCurrency, amount))
                .thenReturn(new ArrayList<>());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            exchangeConfigService.findMatchingConfig(sourceCurrency, targetCurrency, amount);
        });

        assertTrue(exception.getMessage().contains("No exchange configuration found"));
        verify(repository).findMatchingConfig(sourceCurrency, targetCurrency, amount);
    }

    @Test
    @DisplayName("Should find matching configuration at boundary values")
    void testFindMatchingConfig_BoundaryValues() {
        // Given
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal minAmount = new BigDecimal("100.00");
        BigDecimal maxAmount = new BigDecimal("100000.00");

        ExchangeRateConfiguration config = ExchangeRateConfiguration.builder()
                .id("config-1")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .fxRate(new BigDecimal("0.95"))
                .createdAt(LocalDateTime.now())
                .build();

        // Test at minimum boundary
        when(repository.findMatchingConfig(sourceCurrency, targetCurrency, minAmount))
                .thenReturn(List.of(config));

        ExchangeRateConfiguration resultMin = exchangeConfigService.findMatchingConfig(
                sourceCurrency, targetCurrency, minAmount);

        assertNotNull(resultMin);
        assertEquals("config-1", resultMin.getId());

        // Test at maximum boundary
        when(repository.findMatchingConfig(sourceCurrency, targetCurrency, maxAmount))
                .thenReturn(List.of(config));

        ExchangeRateConfiguration resultMax = exchangeConfigService.findMatchingConfig(
                sourceCurrency, targetCurrency, maxAmount);

        assertNotNull(resultMax);
        assertEquals("config-1", resultMax.getId());
    }
}

