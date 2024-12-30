package com.example.mailingservice.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.exceptions.EmailSendException;
import com.example.mailingservice.utils.EmailFeedbackSender;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

public class GmailProviderTest {
  @Mock private JavaMailSender javaMailSender;

  @Mock private RetryRegistry retryRegistry;

  @Mock private EmailFeedbackSender emailFeedback;

  @Mock private Retry retry;

  @Mock private Retry.EventPublisher eventPublisher;

  @Mock private MimeMessage mimeMessage;

  @Mock private ResourceLoader resourceLoader;

  private GmailProvider gmailProvider;
  private EmailRequest testEmail;

  @BeforeEach
  void setUp() {
    // Initialize mocks before each test
    MockitoAnnotations.openMocks(this);

    // Minimal retry setup needed to construct the class
    when(retryRegistry.retry("emailService")).thenReturn(retry);
    when(retry.getEventPublisher()).thenReturn(eventPublisher);
    when(eventPublisher.onRetry(any())).thenReturn(eventPublisher);
    when(eventPublisher.onSuccess(any())).thenReturn(eventPublisher);
    when(eventPublisher.onError(any())).thenReturn(eventPublisher);

    // Mock MimeMessage creation
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    gmailProvider = new GmailProvider(javaMailSender, retryRegistry, emailFeedback);

    // Mock SENDER field using reflection
    try {
      org.springframework.test.util.ReflectionTestUtils.setField(
          gmailProvider, "SENDER", "sender@example.com");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    testEmail =
        EmailRequest.builder()
            .to("test@example.com")
            .subject("Test Subject")
            .content("Test Content")
            .build();
  }

  @Test
  void shouldThrowEmailSendException_WhenEmailSendingFails() throws IOException {
    // ! ================Arrange================
    // * Create a dummy image input stream
    InputStream imageStream = new ByteArrayInputStream("dummy image".getBytes());

    // * Mock the resource loading
    ClassPathResource mockResource = mock(ClassPathResource.class);
    when(mockResource.getInputStream()).thenReturn(imageStream);

    // * Configure the mock to throw MailSendException when send is called
    doThrow(new MailSendException("Failed to send email"))
        .when(javaMailSender)
        .send(any(MimeMessage.class));

    // Act & Assert
    EmailSendException ex =
        assertThrows(EmailSendException.class, () -> gmailProvider.sendEmail(testEmail));

    // Verify the exception message
    assertEquals("Failed to send email", ex.getMessage());

    // Verify that send was called exactly once
    verify(javaMailSender, times(1)).send(any(MimeMessage.class));

    // Verify that createMimeMessage was called
    verify(javaMailSender, times(1)).createMimeMessage();
  }

  @Test
  void shouldThrowMessagingException_WhenCreatingMailFails() throws Exception {
    // Arrange
    doThrow(new MessagingException("Failed to create message"))
        .when(mimeMessage)
        .setContent(any(MimeMultipart.class));

    // Act & Assert
    EmailSendException exception =
        assertThrows(EmailSendException.class, () -> gmailProvider.sendEmail(testEmail));

    assertEquals(exception.getMessage(), "Failed to prepare email");
    verify(javaMailSender, never()).send(any(MimeMessage.class));
  }

  @Test
  void getProviderName_ShouldReturnGmail() {
    assertEquals(gmailProvider.getProviderName(), "GMAIL");
  }
}
