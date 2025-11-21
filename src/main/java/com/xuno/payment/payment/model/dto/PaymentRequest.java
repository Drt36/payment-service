package com.xuno.payment.payment.model.dto;

import com.xuno.payment.common.validation.Sanitized;
import com.xuno.payment.payment.model.valueobject.ReceiverInfo;
import com.xuno.payment.payment.model.valueobject.SenderInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Request DTO for creating a payment", name = "PaymentRequest")
public class PaymentRequest {

    @Schema(description = "Idempotency key to prevent duplicate payments", example = "unique-key-12345")
    private String idempotencyKey;

    @Schema(description = "Sender information", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Sender information is required")
    @Valid
    private SenderInfo sender;

    @Schema(description = "Receiver information", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Receiver information is required")
    @Valid
    private ReceiverInfo receiver;

    @Schema(description = "Source currency code (ISO 4217 format)", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Source currency is required")
    @Sanitized(maxLength = 3, message = "Currency code must be valid")
    private String sourceCurrency;

    @Schema(description = "Target currency code (ISO 4217 format)", example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Target currency is required")
    @Sanitized(maxLength = 3, message = "Currency code must be valid")
    private String targetCurrency;

    @Schema(description = "Source country code (ISO 3166-1 alpha-2)", example = "US", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Source country is required")
    @Sanitized(maxLength = 2, message = "Country code must be valid")
    private String sourceCountry;

    @Schema(description = "Destination country code (ISO 3166-1 alpha-2)", example = "DE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Destination country is required")
    @Sanitized(maxLength = 2, message = "Country code must be valid")
    private String destinationCountry;

    @Schema(description = "Source amount to be transferred", example = "1000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Source amount is required")
    @DecimalMin(value = "0.01", message = "Source amount must be positive")
    private BigDecimal sourceAmount;

    @Schema(description = "Purpose of the payment", example = "Payment for services")
    private String purpose;

    @Schema(description = "Payment corridor", example = "US-EU")
    private String corridor;
}
