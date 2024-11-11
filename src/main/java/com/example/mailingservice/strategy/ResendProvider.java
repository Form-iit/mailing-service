package com.example.mailingservice.strategy;

import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.provider.EmailProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResendProvider implements EmailProvider {

  @Override
  public void sendEmail(EmailRequest email) {
    log.info("Sending email using resend");
  }

  @Override
  public String getProviderName() {
    return "RESEND";
  }
}
