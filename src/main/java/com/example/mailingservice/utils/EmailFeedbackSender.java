package com.example.mailingservice.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailFeedbackSender {

  private final RabbitTemplate rabbitTemplate;
  private final String feedbackQueueName;

  public EmailFeedbackSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
    this.feedbackQueueName = "registrationFeedbackQueue";
  }

  public void send(String correlationId, String status, String errorMessage) {
    Map<String, Object> feedbackMessage = new HashMap<>();
    feedbackMessage.put("correlationId", correlationId);
    feedbackMessage.put("status", status);
    if (errorMessage != null) {
      feedbackMessage.put("error", errorMessage);
    }
    rabbitTemplate.convertAndSend(feedbackQueueName, feedbackMessage);
    log.info(
        "Sent feedback to queue {} with correlationId: {} and status {}",
        feedbackQueueName,
        correlationId,
        status);
  }
}
