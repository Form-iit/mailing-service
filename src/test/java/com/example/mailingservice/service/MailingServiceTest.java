package com.example.mailingservice.service;

import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.exceptions.EmailSendException;
import com.example.mailingservice.provider.EmailProvider;
import com.example.mailingservice.provider.EmailProviderFactory;
import com.example.mailingservice.provider.EmailProviderType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailingServiceTest {
    @Mock
    private EmailProviderFactory emailProviderFactory;
    @Mock
    private EmailProvider emailProvider;

    private MailingService mailingService;

    private EmailRequest request;

    @BeforeEach
    public void setUp() {
        mailingService = new MailingService(emailProviderFactory, EmailProviderType.GMAIL);
        request = EmailRequest.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .build();
    }

    @Test
    void sendEmail_WithDefaultProvider_ShouldUseDefaultProvider() throws EmailSendException {
        // Arrange
        when(emailProviderFactory.getProvider(EmailProviderType.GMAIL))
                .thenReturn(emailProvider);
        doNothing().when(emailProvider).sendEmail(any(EmailRequest.class));

        // Act
        mailingService.sendEmail(request);

        // Assert
        verify(emailProviderFactory).getProvider(EmailProviderType.GMAIL);
        verify(emailProvider).sendEmail(request);
    }

    @Test
    void sendEmail_WithSpecificProvider_ShouldUseSpecifiedProvider() throws EmailSendException {
        // Arrange
        when(emailProviderFactory.getProvider(EmailProviderType.MAILGUN)).thenReturn(emailProvider);
        doNothing().when(emailProvider).sendEmail(any(EmailRequest.class));

        // Act
        mailingService.sendEmail(request, EmailProviderType.MAILGUN);

        // Assert
        verify(emailProviderFactory).getProvider(EmailProviderType.MAILGUN);
        verify(emailProvider).sendEmail(request);
    }

    @Test
    void sendEmail_WhenProviderThrowsException_ShouldPropagateException() {
        // Arrange
        when(emailProviderFactory.getProvider(any())).thenReturn(emailProvider);
        doThrow(new EmailSendException("Failed to send email")).when(emailProvider).sendEmail(any());

        // Act & Assert: Expect an EmailSendException to be thrown
        assertThrows(EmailSendException.class, () -> mailingService.sendEmail(request));
    }
}
