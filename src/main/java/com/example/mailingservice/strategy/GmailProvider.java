package com.example.mailingservice.strategy;

import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.exceptions.EmailSendException;
import com.example.mailingservice.provider.EmailProvider;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GmailProvider implements EmailProvider {
  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String SENDER;

  @Value("${resilience4j.retry.instances.emailService.maxAttempts}")
  private int MAX_ATTEMPTS;

  public GmailProvider(JavaMailSender javaMailSender, RetryRegistry retryRegistry) {
    this.javaMailSender = javaMailSender;
    Retry retry =
        retryRegistry.retry("emailService"); // ? gets the retry identifier from the properties

    // => Subscribe to retry events
    retry
        .getEventPublisher()
        .onRetry(this::logRetryAttempt) // Hook into retry attempts
        .onSuccess(
            event ->
                log.info(
                    "Email sent successfully after {} attempts", event.getNumberOfRetryAttempts()))
        .onError(
            event ->
                log.error(
                    "Failed to send the email after {} attempts",
                    event.getNumberOfRetryAttempts()));
  }

  /*
   *  Logs each retry
   ?  @params: (event) The event generated when a retry is initiated
   => @returns: void
  */
  private void logRetryAttempt(RetryOnRetryEvent event) {
    log.info(
        "Retry attempt {}/{} for sending email", event.getNumberOfRetryAttempts(), MAX_ATTEMPTS);
  }

  /*
   *  Sends an email with retry logic and comprehensive error handling
   ?  @params: (details) The email request containing to, subject, and content
   => @returns: void
   !  @throws: EmailServiceException Wrapped exception containing the root cause
   !  @throws: ConstraintViolationException If the request fails validation
  */
  @Override
  @io.github.resilience4j.retry.annotation.Retry(
      name = "emailService",
      fallbackMethod = "handleRetryFailure")
  public void sendEmail(@Valid EmailRequest details) throws EmailSendException {
    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper mail = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      mail.setFrom(SENDER);
      mail.setTo(details.getTo());
      mail.setSubject(details.getSubject());

      MimeBodyPart htmlBody = createHtmlBody(details.getContent());
      MimeBodyPart imagePart = createImagePart();

      // Combine parts
      MimeMultipart multipart = new MimeMultipart();
      multipart.addBodyPart(htmlBody);
      multipart.addBodyPart(imagePart);
      mimeMessage.setContent(multipart);

      try {
        javaMailSender.send(mimeMessage);
      } catch (MailSendException ex) {
        log.error("Failed to send email: {}", ex.getMessage());
        throw new EmailSendException("Failed to send email", ex);
      }
    } catch (MessagingException ex) {
      log.error("Exception while preparing the mail: {}", ex.getMessage());
      throw new EmailSendException("Failed to prepare email", ex);
    } catch (IOException e) {
      throw new EmailSendException("Failed to load resources", e);
    }
  }

  @Override
  public String getProviderName() {
    return "GMAIL";
  }

  public void handleRetryFailure(EmailRequest details, Throwable ex) throws EmailSendException {
    log.error("All retry attempts failed. Unable to send email to {}", details.getTo());
    throw new EmailSendException(
        String.format("Failed to send email to %s after 3 attempts using Gmail", details.getTo()),
        ex);
  }

  /*
   *  Creates the HTML part of the email
   ?  @params: (content) The HTML body
   => @returns: MimeBodyPart
   !  @throws: MessagingException Wrapped exception containing the root cause
  */
  private MimeBodyPart createHtmlBody(String content) throws MessagingException {
    // Create the HTML part
    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(content, "text/html");
    return htmlPart;
  }

  /*
   *  Creates the Image Part of the email
   ?  @params: (Path) The relative path of the image
   => @returns: MimeBodyPart
   !  @throws: MessagingException wrapped exception containing the root cause
   !  @throws: IOException for the getFile method
  */
  private MimeBodyPart createImagePart() throws MessagingException, IOException {
    // Create the image part
    MimeBodyPart imagePart = new MimeBodyPart();
    // Load image with absolute path in classpath
    InputStream imageStream = getClass().getResourceAsStream("/static/images/img.png");

    if (imageStream == null) {
      log.error("Image not found in JAR at 'static/images/img.png'");
      throw new IOException("Logo resource not found in JAR");
    }

    imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(imageStream, "image/png")));

    imagePart.setHeader("Content-ID", "<image>");
    imagePart.setDisposition(MimeBodyPart.INLINE);
    log.debug("Successfully loaded logo");
    return imagePart;
  }
}
