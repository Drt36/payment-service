package com.xuno.payment.exchangeconfig.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response DTO containing exchange rate configuration details", 
        name = "ExchangeConfigResponse")
public class ExchangeConfigResponse {

    @Schema(description = "Unique identifier of the exchange configuration", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "Source currency code", example = "USD")
    private String sourceCurrency;

    @Schema(description = "Target currency code", example = "EUR")
    private String targetCurrency;

    @Schema(description = "Minimum transaction amount", example = "100.00")
    private BigDecimal minAmount;

    @Schema(description = "Maximum transaction amount", example = "100000.00")
    private BigDecimal maxAmount;

    @Schema(description = "Exchange rate from source to target currency", example = "0.95")
    private BigDecimal fxRate;

    @Schema(description = "Flat fee amount", example = "10.00")
    private BigDecimal feeFlat;

    @Schema(description = "Percentage fee (0-100)", example = "0.04")
    private BigDecimal feePercent;

    @Schema(description = "Timestamp when the configuration was created", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the configuration was last updated", example = "2025-01-15T10:35:00")
    private LocalDateTime updatedAt;
}
