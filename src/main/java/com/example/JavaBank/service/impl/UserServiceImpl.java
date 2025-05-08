package com.example.JavaBank.service.impl;

import com.example.JavaBank.dto.*;
import com.example.JavaBank.entity.User;
import com.example.JavaBank.exception.BankException;
import com.example.JavaBank.repository.UserRepo;
import com.example.JavaBank.utils.AccountUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TransactionService transactionService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        logger.info("Creating account for email " + userRequest.getEmail());
        boolean isEmailExists = userRepo.existsByEmail(userRequest.getEmail());
        if (isEmailExists) {
            logger.error("Account creation failed!! Account already exists for email " + userRequest.getEmail());
            throw new BankException(AccountUtils.ACCOUNT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_EXISTS_CODE, null);
        }

        User newUser = new User();
        newUser.setFirstName(userRequest.getFirstName());
        newUser.setLastName(userRequest.getLastName());
        newUser.setOtherName(userRequest.getOtherName());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPhoneNumber(userRequest.getPhoneNumber());
        newUser.setAlternatePhoneNumber(userRequest.getAlternatePhoneNumber());
        newUser.setAddress(userRequest.getAddress());
        newUser.setStateOfOrigin(userRequest.getStateOfOrigin());
        newUser.setGender(userRequest.getGender());
        newUser.setStatus("active");
        newUser.setAccountNumber(AccountUtils.generateAccountNumber());
        newUser.setAccountBalance(BigDecimal.ZERO);

        User savedUser = userRepo.save(newUser);
        logger.info("Account created successfully for " + userRequest.getEmail());

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(savedUser.getEmail());
        emailDetails.setMessageBody("Congratulations!!! Your account has been created successfully. \n Your first name is " + savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName() + "\n Your account number is " + savedUser.getAccountNumber() + "\n Your account balance is " + savedUser.getAccountBalance());
        emailDetails.setSubject("Account Creation Notification");
        emailService.sendEmailAlert(emailDetails);
        logger.info("Email sent successfully to " + savedUser.getEmail());

        BankResponse response = new BankResponse();
        response.setResponseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE);
        response.setResponseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE);
        response.setAccountInfo(new AccountInfo(savedUser.getAccountNumber(), savedUser.getFirstName(), savedUser.getAccountBalance()));

        return response;
    }

    @Override
    public BankResponse accountBalanceEnquiry(EnquiryRequest enquiryRequest) {
        logger.info("Account balance enquiry for account number " + enquiryRequest.getAccountNumber());
        User foundUser = userRepo.findByAccountNumber(enquiryRequest.getAccountNumber());
        if (foundUser == null) {
            logger.error("Account not found for account number " + enquiryRequest.getAccountNumber());
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.ACCOUNT_FOUND_CODE);
        bankResponse.setResponseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE);
        bankResponse.setAccountInfo(new AccountInfo(foundUser.getAccountNumber(), foundUser.getFirstName(), foundUser.getAccountBalance()));
        return bankResponse;
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        logger.info("Name enquiry for account number " + enquiryRequest.getAccountNumber());
        User foundUser = userRepo.findByAccountNumber(enquiryRequest.getAccountNumber());
        if (foundUser == null) {
            logger.error("Account not found for account number " + enquiryRequest.getAccountNumber());
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAmount(CreditDebitRequest creditDebitRequest) {
        logger.info("Crediting amount for account number " + creditDebitRequest.getAccountNumber());
        User creditToUser = userRepo.findByAccountNumber(creditDebitRequest.getAccountNumber());
        if (creditToUser == null) {
            logger.error("Account not found for account number " + creditDebitRequest.getAccountNumber());
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }

        creditToUser.setAccountBalance(creditToUser.getAccountBalance().add(creditDebitRequest.getAccountBalance()));
        userRepo.save(creditToUser);
        logger.info("Amount credited successfully for account number " + creditDebitRequest.getAccountNumber());

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNumber(creditToUser.getAccountNumber());
        transactionDto.setTransactionType("CREDIT");
        transactionDto.setAmount(creditDebitRequest.getAccountBalance());
        logger.info("Saving transaction for account number " + creditToUser.getAccountNumber());

        transactionService.save(transactionDto);

        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.ACOOUNT_AMOUNT_CREDIT_CODE);
        bankResponse.setResponseMessage(AccountUtils.ACCOUNT_AMOUNT_CREDIT_MESSAGE);
        bankResponse.setAccountInfo(new AccountInfo(creditToUser.getAccountNumber(), creditToUser.getFirstName(), creditToUser.getAccountBalance()));
        return bankResponse;
    }

    @Override
    public BankResponse debitAmount(CreditDebitRequest creditDebitRequest) {
        logger.info("Debiting amount for account number " + creditDebitRequest.getAccountNumber());
        User debitFromUser = userRepo.findByAccountNumber(creditDebitRequest.getAccountNumber());
        if (debitFromUser == null) {
            logger.error("Account not found for account number " + creditDebitRequest.getAccountNumber());
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }

        BigInteger availableBalance = debitFromUser.getAccountBalance().toBigInteger();
        BigInteger debitAmount = creditDebitRequest.getAccountBalance().toBigInteger();

        if (availableBalance.intValue() < debitAmount.intValue()) {
            throw new BankException(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE, AccountUtils.INSUFFICIENT_BALANCE_CODE, null);
        }

        debitFromUser.setAccountBalance(debitFromUser.getAccountBalance().subtract(creditDebitRequest.getAccountBalance()));
        userRepo.save(debitFromUser);
        logger.info("Amount debited successfully for account number " + creditDebitRequest.getAccountNumber());

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNumber(debitFromUser.getAccountNumber());
        transactionDto.setTransactionType("DEBIT");
        transactionDto.setAmount(creditDebitRequest.getAccountBalance());
        transactionService.save(transactionDto);
        logger.info("Saving transaction for account number " + debitFromUser.getAccountNumber());

        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.ACCOUNT_AMOUNT_DEBIT_CODE);
        bankResponse.setResponseMessage(AccountUtils.ACCOUNT_AMOUNT_DEBIT_MESSAGE);
        bankResponse.setAccountInfo(new AccountInfo(debitFromUser.getAccountNumber(), debitFromUser.getFirstName(), debitFromUser.getAccountBalance()));
        return bankResponse;
    }

    @Override
    public BankResponse transferAmount(TransferRequest transferRequest) {
        logger.info("Transferring amount from account number " + transferRequest.getFromAccountNumber() + " to account number " + transferRequest.getToAccountNumber());
        User fromAccountUser = userRepo.findByAccountNumber(transferRequest.getFromAccountNumber());
        if (fromAccountUser == null) {
            logger.error("Account not found for account number " + transferRequest.getFromAccountNumber());
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }

        User toAccountUser = userRepo.findByAccountNumber(transferRequest.getToAccountNumber());
        if (toAccountUser == null) {
            logger.error("Transaction failed!!!. Account not found for account number " + transferRequest.getToAccountNumber());
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }

        if (transferRequest.getAmount().compareTo(fromAccountUser.getAccountBalance()) > 0) {
            logger.error("Transaction failed!!!. Insufficient balance for account number " + transferRequest.getFromAccountNumber());
            throw new BankException(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE, AccountUtils.INSUFFICIENT_BALANCE_CODE, null);
        }

        fromAccountUser.setAccountBalance(fromAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
        userRepo.save(fromAccountUser);
        logger.info("Amount debited successfully for account number " + transferRequest.getFromAccountNumber());

        String fromUserName= fromAccountUser.getFirstName()+" "+fromAccountUser.getLastName()+" "+fromAccountUser.getOtherName();

        EmailDetails debitUser=new EmailDetails();
        debitUser.setSubject("DEBIT ALERT");
        debitUser.setMessageBody("Sum of "+ transferRequest.getAmount() +" has been deducted from your Account " + transferRequest.getFromAccountNumber() + "\n Your Current Account BAlance is "+ fromAccountUser.getAccountBalance());
        debitUser.setRecipient(fromAccountUser.getEmail());
        emailService.sendEmailAlert(debitUser);

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNumber(fromAccountUser.getAccountNumber());
        transactionDto.setTransactionType("DEBIT");
        transactionDto.setAmount(transferRequest.getAmount());
        transactionService.save(transactionDto);
        logger.info("Saving transaction for account number " + fromAccountUser.getAccountNumber());

        toAccountUser.setAccountBalance(toAccountUser.getAccountBalance().add(transferRequest.getAmount()));
        userRepo.save(toAccountUser);
        EmailDetails creditUser=new EmailDetails();
        creditUser.setMessageBody("CREDIT ALERT");
        creditUser.setRecipient(toAccountUser.getEmail());
        creditUser.setMessageBody("Sum of "+ transferRequest.getAmount()+" has been credited your account. \n"+ " Your Current Balance is "+toAccountUser.getAccountBalance() );
        emailService.sendEmailAlert(creditUser);
        logger.info("Email sent to account holder for account number " + toAccountUser.getAccountNumber());

        transactionDto.setAccountNumber(toAccountUser.getAccountNumber());
        transactionDto.setTransactionType("CREDIT");
        transactionService.save(transactionDto);

        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.TRANSFER_SUCCESS_CODE);
        bankResponse.setResponseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE);
        bankResponse.setAccountInfo(null);
        return bankResponse;
    }

    @Override
    public BankResponse deleteAccount(String accountNumber) {
        logger.info("Deleting account for account number " + accountNumber);
        User user = userRepo.findByAccountNumber(accountNumber);
        if (user == null) {
            logger.error("Account not found for account number " + accountNumber);
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }
        userRepo.delete(user);
        logger.info("Account deleted for account number " + accountNumber);
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.ACCOUNT_DELETED_CODE);
        bankResponse.setResponseMessage(AccountUtils.ACCOUNT_DELETED_MESSAGE);
        bankResponse.setAccountInfo(null);
        return bankResponse;
    }

    @Override
    public BankResponse getAccountByAccountNumber(String accountNumber) {
        logger.info("Getting account details for account number " + accountNumber);
        User user = userRepo.findByAccountNumber(accountNumber);
        if (user == null) {
            logger.error("Account not found for account number " + accountNumber);
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.ACCOUNT_FOUND_CODE);
        bankResponse.setResponseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE);
        bankResponse.setAccountInfo(new AccountInfo(user.getAccountNumber(), user.getFirstName(), user.getAccountBalance()));
        return bankResponse;
    }

    @Override
    public BankResponse updateAccount(String accountNumber, UserRequest userRequest) {
        logger.info("Updating account for account number " + accountNumber);
        User user = userRepo.findByAccountNumber(accountNumber);
        if (user == null) {
            logger.error("Account not found for account number " + accountNumber);
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setOtherName(userRequest.getOtherName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setAlternatePhoneNumber(userRequest.getAlternatePhoneNumber());
        user.setAddress(userRequest.getAddress());
        user.setStateOfOrigin(userRequest.getStateOfOrigin());
        user.setGender(userRequest.getGender());
        user.setEmail(userRequest.getEmail());
        userRepo.save(user);
        logger.info("Account updated for account number " + accountNumber);
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.ACCOUNT_UPDATED_CODE);
        bankResponse.setResponseMessage(AccountUtils.ACCOUNT_UPDATED_MESSAGE);
        bankResponse.setAccountInfo(new AccountInfo(user.getAccountNumber(), user.getFirstName(), user.getAccountBalance()));
        return bankResponse;
    }

    @Override
    public BankResponse patchAccount(String accountNumber, Map<String, Object> userRequest) {
        logger.info("Updating account for account number " + accountNumber);
        User existingUser = userRepo.findByAccountNumber(accountNumber);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try{
            mapper.updateValue(existingUser, userRequest);
        }catch (Exception e){
            logger.error("Account not found for account number " + accountNumber);
            throw new BankException(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, AccountUtils.ACCOUNT_NOT_EXISTS_CODE, null);
        }

        userRepo.save(existingUser);
        logger.info("Account updated for account number " + accountNumber);
        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseCode(AccountUtils.ACCOUNT_UPDATED_CODE);
        bankResponse.setResponseMessage(AccountUtils.ACCOUNT_UPDATED_MESSAGE);
        bankResponse.setAccountInfo(new AccountInfo(existingUser.getAccountNumber(), existingUser.getFirstName(), existingUser.getAccountBalance()));
        return bankResponse;
    }
}
