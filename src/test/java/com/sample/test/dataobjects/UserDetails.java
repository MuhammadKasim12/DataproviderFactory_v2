package com.sample.test.dataobjects;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public @Data class UserDetails {

    private String testCase;
    private String name = null;
    private String password = null;
    private Long accountNumber = null;
    private Double amount = null;
    private AreaCode[] areaCode = null;
    private BankInformation[] bank = null;
    private String phoneNumber = null;
    private int preintTest = 0;
    private boolean isbooleanGood = false;
    private double doubleTest = 0.0;
    private long longTest = 0;
    private float floatTest = (float) 0.0;
    private byte byteTest;

}
