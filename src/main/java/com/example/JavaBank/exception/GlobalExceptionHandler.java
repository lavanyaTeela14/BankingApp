package com.example.JavaBank.exception;

import com.example.JavaBank.dto.BankResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankException.class)
    public ResponseEntity<BankResponse> handleBankException(BankException e) {
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(e.getErrorCode());
        bankResponse.setResponseMessage(e.getMessage());
        bankResponse.setAccountInfo(e.getAccountInfo());
        return new ResponseEntity<>(bankResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<BankResponse> handleEmailSendingException(EmailSendingException e) {
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode("403");
        bankResponse.setResponseMessage(e.getMessage());
        bankResponse.setAccountInfo(null);
        return new ResponseEntity<>(bankResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<BankResponse> handleTransactionException(TransactionException e) {
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode("404");
        bankResponse.setResponseMessage(e.getMessage());
        bankResponse.setAccountInfo(null);
        return new ResponseEntity<>(bankResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BankResponse> handleGenericException(Exception e) {
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode("500");
        bankResponse.setResponseMessage("An unexpected error occurred: " + e.getMessage());
        bankResponse.setAccountInfo(null);
        return new ResponseEntity<>(bankResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
