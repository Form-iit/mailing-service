package com.example.mailingservice.config.rabbitMQ;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EnvConfig {
  @Bean
  public QueueProperties queueProperties(Environment env, ObjectMapper objectMapper)
      throws Exception {
    String queuesJson = env.getProperty("spring.rabbitmq.queues");
    QueueProperties properties = new QueueProperties();
    if (queuesJson != null) {
      List<QueueConfig> configs =
          objectMapper.readValue(
              queuesJson,
              objectMapper.getTypeFactory().constructCollectionType(List.class, QueueConfig.class));
      properties.setQueues(configs);
    }
    return properties;
  }
}
