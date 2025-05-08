package com.example.JavaBank.controller;

import com.example.JavaBank.entity.Transaction;
import com.example.JavaBank.exception.TransactionException;
import com.example.JavaBank.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("bankStatements")
public class TransactionController {
    private BankStatement bankStatement;

    public TransactionController(BankStatement bankStatement) {
        this.bankStatement = bankStatement;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> generateStatements(@RequestParam String accountNumber,
                                                @RequestParam String startDate,
                                                @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        try{
            List<Transaction> transactions=bankStatement.generateStatement(accountNumber,startDate,endDate);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        }
        catch (DocumentException | FileNotFoundException e){
            throw new TransactionException("Error in generating bank statement"+e.getMessage());
        }
        catch (Exception e){
            throw new TransactionException("Unexpected Error in generating bank statement"+e.getMessage());
        }
    }
}
