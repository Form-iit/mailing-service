package com.example.mailingservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class EmailRequest {
  private String correlationId;

  @NotEmpty(message = "Email recipient can't be empty")
  @Email(
      regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
      message = "Invalid email format")
  private String to;

  @NotEmpty(message = "The subject cannot be empty")
  private String subject;

  @NotEmpty(message = "The email's content cannot be empty")
  private String content;
}
