package com.example.mailingservice.consumer;

import com.example.mailingservice.config.rabbitMQ.QueueProperties;
import com.example.mailingservice.dto.EmailRequest;
import com.example.mailingservice.service.MailingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailConsumer {

    private final MailingService senderService;

    @Autowired
    public EmailConsumer(MailingService senderService) {
        this.senderService = senderService;
    }

    @RabbitListener(queues = "#{@queues.values().![name]}", containerFactory = "rabbitListenerContainerFactory")
    public void receiveEmail(EmailRequest email) {
        log.info("Received email content");
        senderService.sendEmail(email);
    }
}