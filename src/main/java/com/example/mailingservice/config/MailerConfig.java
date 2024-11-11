package com.example.mailingservice.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailerConfig {
  @Bean
  public JavaMailSender javaMailSender(
      @Value("${spring.mail.host}") String host,
      @Value("${spring.mail.port}") int port,
      @Value("${spring.mail.username}") String username,
      @Value("${spring.mail.password}") String password,
      @Value("${spring.mail.properties.mail.smtp.auth}") Boolean smtpAuth,
      @Value("${spring.mail.properties.mail.smtp.starttls.enable}") Boolean enableStartTls) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", smtpAuth);
    props.put("mail.smtp.starttls.enable", enableStartTls);
    props.put("mail.debug", "false");

    return mailSender;
  }
}
