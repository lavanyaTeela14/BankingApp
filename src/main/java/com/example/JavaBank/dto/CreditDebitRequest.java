package com.example.JavaBank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class CreditDebitRequest {
    private String accountNumber;
    private BigDecimal accountBalance;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
}
