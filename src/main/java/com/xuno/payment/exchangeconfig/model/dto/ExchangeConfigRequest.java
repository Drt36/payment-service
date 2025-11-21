package com.xuno.payment.exchangeconfig.model.dto;

import com.xuno.payment.common.validation.Sanitized;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating exchange rate configuration",
        name = "ExchangeConfigRequest")
public class ExchangeConfigRequest {

    @Schema(
            description = "Source currency code (ISO 4217 format)",
            example = "USD",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Source currency is required")
    @Sanitized(maxLength = 3, message = "Currency code must be valid")
    private String sourceCurrency;

    @Schema(
            description = "Target currency code (ISO 4217 format)",
            example = "EUR",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Target currency is required")
    @Sanitized(maxLength = 3, message = "Currency code must be valid")
    private String targetCurrency;

    @Schema(
            description = "Minimum transaction amount for this exchange rate configuration",
            example = "100.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.01", message = "Minimum amount must be positive")
    private BigDecimal minAmount;

    @Schema(
            description = "Maximum transaction amount for this exchange rate configuration",
            example = "100000.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.01", message = "Maximum amount must be positive")
    private BigDecimal maxAmount;

    @Schema(
            description = "Exchange rate from source currency to target currency",
            example = "0.95",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Exchange rate is required")
    @DecimalMin(value = "0.01", message = "Exchange rate must be positive")
    private BigDecimal fxRate;

    @Schema(
            description = "Flat fee amount applied to transactions (optional)",
            example = "10.00"
    )
    @DecimalMin(value = "0.00", message = "Flat fee must be zero or positive")
    private BigDecimal feeFlat;

    @Schema(
            description = "Percentage fee applied to transactions (0-100, e.g., 0.04 = 4%)",
            example = "0.04"
    )
    @DecimalMin(value = "0", message = "Fee percent must be >= 0")
    @DecimalMax(value = "100", message = "Fee percent must be <= 100")
    private BigDecimal feePercent;

}
