package com.xuno.payment.payment.model.dto;

import com.xuno.payment.payment.model.enums.PaymentStatus;
import com.xuno.payment.payment.model.valueobject.ExchangeRateCalculationResult;
import com.xuno.payment.payment.model.valueobject.FeeCalculationResult;
import com.xuno.payment.payment.model.valueobject.ReceiverInfo;
import com.xuno.payment.payment.model.valueobject.SenderInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO containing payment details", name = "PaymentResponse")
public class PaymentResponse {

    @Schema(description = "Unique identifier of the payment", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "Payment reference number", example = "TXN-20250121-143022-12345")
    private String referenceNumber;

    @Schema(description = "Sender information")
    private SenderInfo sender;

    @Schema(description = "Receiver information")
    private ReceiverInfo receiver;

    @Schema(description = "Source currency code", example = "USD")
    private String sourceCurrency;

    @Schema(description = "Target currency code", example = "EUR")
    private String targetCurrency;

    @Schema(description = "Source country code", example = "US")
    private String sourceCountry;

    @Schema(description = "Destination country code", example = "DE")
    private String destinationCountry;

    @Schema(description = "Source amount", example = "1000.00")
    private BigDecimal sourceAmount;

    @Schema(description = "Target amount after conversion and fees", example = "940.00")
    private BigDecimal targetAmount;

    @Schema(description = "Purpose of the payment", example = "Payment for services")
    private String purpose;

    @Schema(description = "Payment corridor", example = "US-EU")
    private String corridor;

    @Schema(description = "Current payment status", example = "PENDING_ADMIN_REVIEW")
    private PaymentStatus status;

    @Schema(description = "Exchange rate calculation result")
    private ExchangeRateCalculationResult exchangeRateCalculation;

    @Schema(description = "Fee calculation result")
    private FeeCalculationResult feeCalculation;

    @Schema(description = "ID of the admin who created the payment", example = "admin123")
    private String createdBy;

    @Schema(description = "ID of the admin who validated the payment", example = "admin123")
    private String validatedBy;

    @Schema(description = "Whether the payment was verified by the system", example = "true")
    private boolean systemVerified;

    @Schema(description = "Timestamp when the payment was created", example = "2025-01-21T14:30:22")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the payment was validated", example = "2025-01-21T14:35:00")
    private LocalDateTime validatedAt;

    @Schema(description = "Estimated delivery date", example = "2025-01-23T14:35:00")
    private LocalDateTime estimatedDeliveryDate;

    @Schema(description = "Timestamp when the payment was last updated", example = "2025-01-21T14:35:00")
    private LocalDateTime updatedAt;
}
