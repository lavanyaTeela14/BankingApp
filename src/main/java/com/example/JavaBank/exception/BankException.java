package com.example.JavaBank.exception;

import com.example.JavaBank.dto.AccountInfo;

public class BankException extends RuntimeException {
    private final String errorCode;
    private final AccountInfo accountInfo;

    public BankException(String message, String errorCode, AccountInfo accountInfo) {
        super(message);
        this.errorCode = errorCode;
        this.accountInfo = accountInfo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }
    
}
