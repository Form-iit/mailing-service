package com.example.mailingservice.provider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EmailProviderFactory {
  private final Map<EmailProviderType, EmailProvider> providers;

  public EmailProviderFactory(List<EmailProvider> emailProviders) {
    providers =
        emailProviders.stream()
            .collect(
                Collectors.toMap(
                    provider -> EmailProviderType.valueOf(provider.getProviderName().toUpperCase()),
                    provider -> provider));
  }

  public EmailProvider getProvider(EmailProviderType type) {
    EmailProvider provider = providers.get(type);
    if (provider == null) {
      throw new IllegalArgumentException("Unsupported email provider: " + type);
    }
    return provider;
  }
}
