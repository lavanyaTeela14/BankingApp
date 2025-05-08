package com.example.JavaBank.service.impl;

import com.example.JavaBank.dto.EmailDetails;
import com.example.JavaBank.exception.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        if(emailDetails == null || emailDetails.getRecipient() == null || emailDetails.getMessageBody() == null)
        {
            log.error("Email details are incomplete or null" + emailDetails);
            throw new EmailSendingException("Email details are incomplete or null");
        }
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setSubject(emailDetails.getSubject());
            mailMessage.setText(emailDetails.getMessageBody());
            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to " + emailDetails.getRecipient());
        }catch (Exception e){
            log.error("Error sending email to " + emailDetails.getRecipient(), e);
            throw new EmailSendingException("Error sending email to " + emailDetails.getRecipient());
        }
    }

    @Override
    public void sendEmailBankStatement(EmailDetails emailDetails) {
        if(emailDetails == null || emailDetails.getRecipient() == null || emailDetails.getMessageBody() == null)
        {
            log.error("Email details are incomplete or null" + emailDetails);
            throw new EmailSendingException("Email details are incomplete or null");
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try{
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            mimeMessageHelper.setText(emailDetails.getMessageBody(), true);

            FileSystemResource file = new FileSystemResource(emailDetails.getAttachment());
            if(!file.exists()){
                log.error("Attachment file not found: " + emailDetails.getAttachment());
                throw new EmailSendingException("Attachment file not found: " + emailDetails.getAttachment());
            }
            mimeMessageHelper.addAttachment(file.getFilename(), file);

            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to " + emailDetails.getRecipient());
        }catch (Exception e){
            log.error("Error sending email bank statement to " + emailDetails.getRecipient(), e);
            throw new EmailSendingException("Error sending email bank statement to " + emailDetails.getRecipient());
        }
    }
}
