package com.xuno.payment.payment.model.valueobject;

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
public class SenderInfo {
    private String name;
    private String address;
    private String referenceNumber;
    private SenderFundingAccountInfo fundingAccount;
}
