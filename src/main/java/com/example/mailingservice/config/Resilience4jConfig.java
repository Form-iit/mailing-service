package com.example.mailingservice.config;

import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jConfig {
  @Bean
  public RetryRegistry retryRegistry() {
    return RetryRegistry.ofDefaults();
  }
}
