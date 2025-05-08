package com.example.JavaBank.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_MESSAGE = "Account already exists";
    public static final String ACCOUNT_EXISTS_CODE="001";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE="002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account created successfully";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE="Account not found";
    public static final String ACCOUNT_NOT_EXISTS_CODE="003";
    public static final String ACCOUNT_FOUND_MESSAGE="Account found";
    public static final String ACCOUNT_FOUND_CODE="004";
    public static final String ACOOUNT_AMOUNT_CREDIT_CODE="005";
    public static final String ACCOUNT_AMOUNT_CREDIT_MESSAGE="Amount credited succesfully!!!";
    public static final String INSUFFICIENT_BALANCE_CODE="006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE="Insufficient Money";
    public static final String DEBITED_CODE="007";
    public static final String ACCOUNT_AMOUNT_DEBIT_CODE="007";
    public static final String ACCOUNT_AMOUNT_DEBIT_MESSAGE="Amount debited from the account!";
    public static final String TRANSFER_SUCCESS_CODE="008";
    public static final String TRANSFER_SUCCESS_MESSAGE="Amount has been transferred successfully!!";
    public static final String ACCOUNT_DELETED_CODE="009";
    public static final String ACCOUNT_DELETED_MESSAGE="Account deleted successfully!!";
    public static final String ACCOUNT_UPDATED_CODE = "010";
    public static final String ACCOUNT_UPDATED_MESSAGE = "Account updated successfully!!";

    public static String generateAccountNumber() {
        Year currentYear = Year.now();
        int min=100000;
        int max=999999;
        int randomNum = (int) Math.floor(Math.random()*(max-min+1)+min);
        return String.valueOf(currentYear.getValue()) + String.valueOf(randomNum);
    }
}
