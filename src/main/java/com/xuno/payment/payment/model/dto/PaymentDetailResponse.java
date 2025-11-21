package com.xuno.payment.payment.model.dto;

import com.xuno.payment.payment.model.valueobject.StatusHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed response DTO containing payment details with status history", name = "PaymentDetailResponse")
public class PaymentDetailResponse extends PaymentResponse {

    @Schema(description = "Status change history for the payment", example = "[]")
    private List<StatusHistory> statusHistory;
}

