package com.example.JavaBank.service.impl;

import com.example.JavaBank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailBankStatement(EmailDetails emailDetails);
}
