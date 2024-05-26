package com.sample.test.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
public @Data class BankInformation {

    private String bankName;
    private String bankType;
    private String bankAddress;

    
}
