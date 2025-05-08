package com.example.JavaBank.service.impl;

import com.example.JavaBank.dto.*;
import com.example.JavaBank.entity.User;
import com.example.JavaBank.repository.UserRepo;
import com.example.JavaBank.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepo userRepo;
    @Mock
    TransactionService transactionService;
    @Mock
    EmailService emailService;
    @InjectMocks
    UserServiceImpl userService;

    private UserRequest userRequest;
    private EnquiryRequest enquiryRequest;
    private CreditDebitRequest creditDebitRequest;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setGender("male");
        userRequest.setAddress("123 Main St");
        userRequest.setStateOfOrigin("Lagos");
        userRequest.setPhoneNumber("1234567890");
        userRequest.setEmail("j@j.com");
        userRequest.setAlternatePhoneNumber("1234567890");

        enquiryRequest = new EnquiryRequest("1234567890");

        creditDebitRequest = new CreditDebitRequest();
        creditDebitRequest.setAccountNumber("1234567890");
        creditDebitRequest.setAccountBalance(BigDecimal.valueOf(1000));

        /*transferRequest = new TransferRequest();
        transferRequest.setFromAccountNumber("1234567890");
        transferRequest.setToAccountNumber("1234567890");
        transferRequest.setAmount(BigDecimal.valueOf(1000));*/
    }
    @Test
    void createAccountSuccess() {
        User user = new User();
        user.setFirstName("John");
        when(userRepo.save(user)).thenReturn(user);
        User savedUser=userRepo.save(user);
        assertEquals(savedUser,user);
        assertEquals(savedUser.getFirstName(),user.getFirstName());
    }
}