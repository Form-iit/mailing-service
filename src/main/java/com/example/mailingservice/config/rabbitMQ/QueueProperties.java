package com.example.mailingservice.config.rabbitMQ;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.queues")
@Getter
@Setter
public class QueueProperties {
  private List<QueueConfig> queues;
}
