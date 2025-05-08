package com.example.JavaBank.service.impl;

import com.example.JavaBank.dto.*;

import java.util.Map;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);

    BankResponse accountBalanceEnquiry(EnquiryRequest enquiryRequest);

    String nameEnquiry(EnquiryRequest enquiryRequest);

    BankResponse creditAmount(CreditDebitRequest creditDebitRequest);

    BankResponse debitAmount(CreditDebitRequest creditDebitRequest);

    BankResponse transferAmount(TransferRequest creditDebitRequest);

    BankResponse deleteAccount(String accountNumber);

    BankResponse getAccountByAccountNumber(String accountNumber);

    BankResponse updateAccount(String accountNumber,UserRequest userRequest);

    BankResponse patchAccount(String accountNumber, Map<String, Object> userRequest);
}
