package com.xuno.payment.payment.model.valueobject;

import com.xuno.payment.common.validation.AccountNumber;
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
public class ReceiverAccountInfo {
    
    @AccountNumber
    private String accountNumber;
    
    private String bankCode;
    
    private String swiftCode;
}

