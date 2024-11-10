package com.example.mailingservice.config.rabbitMQ;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.queues")
@Getter
@Setter
public class QueueProperties {
    private List<QueueConfig> queues;
}
