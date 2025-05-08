package com.example.JavaBank.service.impl;

import com.example.JavaBank.dto.EmailDetails;
import com.example.JavaBank.entity.Transaction;
import com.example.JavaBank.entity.User;
import com.example.JavaBank.exception.BankException;
import com.example.JavaBank.repository.TransactionRepo;
import com.example.JavaBank.repository.UserRepo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service

public class BankStatement {

    private static final Logger log = LoggerFactory.getLogger(BankStatement.class);
    private static final String FILE="C:\\Users\\DELL\\Documents\\MyBankStatement.pdf";
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    private UserRepo userRepo;

    public BankStatement(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public TransactionRepo getTransactionRepo() {
        return transactionRepo;
    }

    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        log.info("Generating bank statement for account number from {} to {}" + accountNumber,startDate,endDate);

        LocalDate start;
        LocalDate end;
        try{
            start= LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            end=LocalDate.parse(endDate,DateTimeFormatter.ISO_DATE);
        }catch (Exception e){
            throw new BankException("Invalid date format. Please use YYYY-MM-DD.","405",null);
        }

        List<Transaction> transactionList=transactionRepo.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> !transaction.getCreatedAt().isBefore(start) && !transaction.getCreatedAt().isAfter(end))
                .toList();

        User user=userRepo.findByAccountNumber(accountNumber);
        if(user==null)
        {
            log.error("User not found for account number "+accountNumber);
            throw new BankException("User not found for account number "+accountNumber,"404",null);
        }
        String customerName=user.getFirstName()+" "+user.getLastName() + " " + user.getOtherName();

        Rectangle statementSize=new Rectangle(PageSize.A4);
        Document statement=new Document(statementSize);
        OutputStream outputStream=new FileOutputStream(FILE);
        PdfWriter.getInstance(statement,outputStream);
        statement.open();

        Paragraph bankName=new Paragraph("The Java Bank");
        bankName.setAlignment(Element.ALIGN_CENTER);
        statement.add(bankName);

        Paragraph address=new Paragraph("123 Main Street, Anytown, USA");
        address.setAlignment(Element.ALIGN_CENTER);
        statement.add(address);

        Paragraph customer=new Paragraph("Customer Name: "+customerName);
        customer.setAlignment(Element.ALIGN_RIGHT);
        statement.add(customer);

        Paragraph account=new Paragraph("Account Number: "+accountNumber);
        account.setAlignment(Element.ALIGN_RIGHT);
        statement.add(account);

        Paragraph customerAddress=new Paragraph("Customer Address: "+user.getAddress());
        customerAddress.setAlignment(Element.ALIGN_RIGHT);
        statement.add(customerAddress);

        Paragraph statementDate=new Paragraph("Statement Date: "+start+" to "+end);
        statementDate.setAlignment(Element.ALIGN_CENTER);
        statement.add(statementDate);

        Paragraph title=new Paragraph("MyBank Statement");
        title.setAlignment(Element.ALIGN_CENTER);
        statement.add(title);

        PdfPTable table=new PdfPTable(4);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        PdfPCell cell=new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        cell.setPaddingLeft(5);
        cell.setPaddingRight(5);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);

        cell.setPhrase(new Phrase("Transaction Date"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Transaction Type"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Amount"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Status"));
        table.addCell(cell);

        for(Transaction transaction:transactionList){
            cell.setPhrase(new Phrase(transaction.getCreatedAt().toString()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(transaction.getTransactionType()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(transaction.getAmount().toString()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(transaction.getStatus()));
            table.addCell(cell);
        }

        statement.add(table);

        Paragraph AccountBalance=new Paragraph("Account Balance: "+user.getAccountBalance());
        AccountBalance.setAlignment(Element.ALIGN_RIGHT);
        statement.add(AccountBalance);

        statement.close();

        EmailDetails emailDetails=new EmailDetails();
        emailDetails.setRecipient(user.getEmail());
        emailDetails.setSubject("Bank Statement");
        emailDetails.setMessageBody("Attached is your MyBank Statement");
        emailDetails.setAttachment(FILE);
        emailService.sendEmailBankStatement(emailDetails);
        log.info("Email sent successfully to " + user.getEmail());

        return transactionList;

        /**
    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start= LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end=LocalDate.parse(endDate,DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList=transactionRepo.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> !transaction.getCreatedAt().isBefore(start) && !transaction.getCreatedAt().isAfter(end))
                .toList();

        User user=userRepo.findByAccountNumber(accountNumber);
        String customerName=user.getFirstName()+" "+user.getLastName() + " " + user.getOtherName();

        Rectangle statementSize=new Rectangle(PageSize.A4);
        Document document=new Document(statementSize);
        log.info("Setting size of statement!!");
        OutputStream outputStream= new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankInfoTable=new PdfPTable(1);
        PdfPCell bankName=new PdfPCell(new Phrase("The Java Bank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress=new PdfPCell(new Phrase("Kolkata, West Bengal, India"));
        bankAddress.setBorder(0);
        bankAddress.setBackgroundColor(BaseColor.BLUE);
        bankAddress.setPadding(20f);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo=new PdfPTable(2);
        PdfPCell customerInfo=new PdfPCell(new Phrase("Start Date "+startDate));
        customerInfo.setBorder(0);

        PdfPCell statement=new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);

        PdfPCell endDateInfo=new PdfPCell(new Phrase("End Date "+endDate));
        endDateInfo.setBorder(0);

        PdfPCell customerNameInfo=new PdfPCell(new Phrase("Customer Name "+customerName));
        customerNameInfo.setBorder(0);

        PdfPCell spacer=new PdfPCell(new Phrase(" "));
        spacer.setBorder(0);

        PdfPCell address=new PdfPCell(new Phrase("Address "+user.getAddress()));
        address.setBorder(0);

        PdfPTable statementTable=new PdfPTable(4);
        statementTable.addCell(new Phrase("Date"));
        statementTable.addCell(new Phrase("Transaction ID"));
        statementTable.addCell(new Phrase("Transaction Type"));
        statementTable.addCell(new Phrase("Amount"));
        for(Transaction transaction:transactionList){
            statementTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            statementTable.addCell(new Phrase(transaction.getTransactionId()));
            statementTable.addCell(new Phrase(transaction.getTransactionType()));
            statementTable.addCell(new Phrase(transaction.getAmount().toString()));
        }
        statementInfo.addCell(customerInfo);
        statementInfo.addCell(spacer);
        statementInfo.addCell(statement);
        statementInfo.addCell(spacer);
        statementInfo.addCell(endDateInfo);
        statementInfo.addCell(spacer);
        statementInfo.addCell(customerNameInfo);
        statementInfo.addCell(spacer);
        statementInfo.addCell(address);
        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(statementTable);
        document.close();
        return transactionList;
    }
}
*/
    }
}
