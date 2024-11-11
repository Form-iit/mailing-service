package com.example.mailingservice.config.rabbitMQ;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueueConfig {
  private String type;
  private String name;
}
