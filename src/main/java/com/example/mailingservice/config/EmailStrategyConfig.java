package com.example.mailingservice.config;

import com.example.mailingservice.provider.EmailProviderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EmailStrategyConfig {

    @Bean("defaultEmailProviderType")
    public EmailProviderType defaultEmailProviderType() {
        return EmailProviderType.GMAIL;
    }
}
