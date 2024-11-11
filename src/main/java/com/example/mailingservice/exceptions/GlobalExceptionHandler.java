package com.example.mailingservice.exceptions;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  private final Map<String, Object> responseBody = new HashMap<>();

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    responseBody.clear();
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    responseBody.put("type", "ValidationError");
    responseBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
    responseBody.put("errors", errors);
    responseBody.put("status", HttpStatus.BAD_REQUEST.value());

    log.error("Validation errors: {}", errors);
    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(value = {EmailSendException.class})
  public ResponseEntity<Object> handleEmailSendException(Exception ex) {
    responseBody.clear();
    Throwable cause = ex;
    while (ex.getCause() != null) {
      cause = ex.getCause();
    }
    String exceptionClassName = cause.getClass().getSimpleName();
    responseBody.put("type", exceptionClassName);
    responseBody.put("error", ex.getMessage());

    HttpStatus status =
        exceptionClassName.equals("MessagingException")
            ? HttpStatus.BAD_REQUEST
            : HttpStatus.INTERNAL_SERVER_ERROR;
    responseBody.put("status", status.value());

    log.error("Email sending error: {}", ex.getMessage());

    return ResponseEntity.status(status).body(responseBody);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception ex) {
    responseBody.clear();
    Throwable cause = ex;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    String exceptionClassName = cause.getClass().getSimpleName();
    responseBody.put("type", exceptionClassName);
    responseBody.put("error", ex.getMessage());
    log.error("Email sending error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
  }

  /*@ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
      log.error("Resource not found", ex);
      responseBody.clear();
      responseBody.put("type", "ResourceNotFoundException");
      responseBody.put("error", ex.getMessage());

      HttpStatus status = HttpStatus.NOT_FOUND;
      responseBody.put("status", status.value());

      return ResponseEntity.status(status).body(responseBody);
  }*/
}
