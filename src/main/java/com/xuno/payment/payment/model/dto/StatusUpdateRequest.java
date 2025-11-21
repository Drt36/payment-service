package com.xuno.payment.payment.model.dto;

import com.xuno.payment.payment.model.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating payment status", name = "StatusUpdateRequest")
public class StatusUpdateRequest {

    @Schema(description = "New payment status", example = "APPROVED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Status is required")
    private PaymentStatus status;

    @Schema(description = "Optional note for the status change", example = "Reviewed and validated by admin")
    private String note;
}

