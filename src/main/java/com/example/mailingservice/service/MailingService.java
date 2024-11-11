package com.example.mailingservice.service;

import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.exceptions.EmailSendException;
import com.example.mailingservice.provider.EmailProvider;
import com.example.mailingservice.provider.EmailProviderFactory;
import com.example.mailingservice.provider.EmailProviderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailingService {
  private final EmailProviderFactory emailProviderFactory;
  private final EmailProviderType defaultProviderType;

  public MailingService(
      EmailProviderFactory emailProviderFactory,
      @Qualifier("defaultEmailProviderType") EmailProviderType defaultProviderType) {
    this.emailProviderFactory = emailProviderFactory;
    this.defaultProviderType = defaultProviderType;
  }

  public void sendEmail(EmailRequest request) throws EmailSendException {
    sendEmail(request, defaultProviderType);
  }

  public void sendEmail(EmailRequest request, EmailProviderType providerType)
      throws EmailSendException {
    EmailProvider provider = emailProviderFactory.getProvider(providerType);
    log.info("Sending email using provider: {}", provider.getProviderName());
    provider.sendEmail(request);
  }
}
