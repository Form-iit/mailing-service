package com.example.mailingservice.consumer;

import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.service.MailingService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailConsumer {

  private final MailingService senderService;

  public EmailConsumer(MailingService senderService) {
    this.senderService = senderService;
  }

  @RabbitListener(
      queues = "registrationMailQueue",
      containerFactory = "rabbitListenerContainerFactory")
  public void receiveEmail(EmailRequest email, Message message, Channel channel) throws Exception {
    try {
      log.info("Received email content with correlation ID: {}", email.getCorrelationId());
      senderService.sendEmail(email);
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    } catch (Exception e) {
      log.error("Error processing message: {}", e.getMessage());
      channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
    }
  }
}
