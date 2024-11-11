package com.example.mailingservice.config.rabbitMQ;

import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
  private final QueueProperties queueProperties;
  private final ConfigurableBeanFactory beanFactory;

  RabbitConfig(QueueProperties queueProperties, ConfigurableBeanFactory beanFactory) {
    this.queueProperties = queueProperties;
    this.beanFactory = beanFactory;
  }

  @Bean
  RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory); // Define RabbitAdmin bean with ConnectionFactory
  }

  @Bean
  Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }

  @Bean
  Map<String, Queue> queues(RabbitAdmin rabbitAdmin) { // Inject RabbitAdmin here
    Map<String, Queue> queues = new HashMap<>();
    queueProperties
        .getQueues()
        .forEach(
            queueConfig -> {
              Queue queue =
                  new Queue(queueConfig.getName(), false); // Non-durable queue as an example
              if (rabbitAdmin.getQueueProperties(queueConfig.getName()) == null) {
                // Only declare if it doesn't exist
                rabbitAdmin.declareQueue(queue);
              }
              beanFactory.registerSingleton(
                  queueConfig.getType(), queue); // Register as Spring Bean for use elsewhere
              queues.put(queueConfig.getType(), queue);
            });
    return queues;
  }
}
