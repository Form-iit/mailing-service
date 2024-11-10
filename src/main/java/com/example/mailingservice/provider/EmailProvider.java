package com.example.mailingservice.provider;

import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.exceptions.EmailSendException;

public interface EmailProvider{
    void sendEmail(EmailRequest email) throws EmailSendException;
    String getProviderName();
}
