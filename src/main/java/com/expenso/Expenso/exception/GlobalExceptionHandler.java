package com.expenso.Expenso.exception;

import com.expenso.Expenso.exception.custom.EmailAlreadyExistsException;
import com.expenso.Expenso.exception.custom.InvalidOtpException;
import com.expenso.Expenso.exception.custom.UserDisabledException;
import com.expenso.Expenso.exception.custom.InvalidRequestException;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.response.CustomResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<CustomResponseMessage> handleEmailExists(EmailAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
                         .body(new CustomResponseMessage(false, ex.getMessage()));
  }

  @ExceptionHandler(InvalidOtpException.class)
  public ResponseEntity<CustomResponseMessage> handleInvalidOtp(InvalidOtpException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new CustomResponseMessage(false, ex.getMessage()));
  }

  @ExceptionHandler(UserDisabledException.class)
  public ResponseEntity<CustomResponseMessage> handleUserDisabled(UserDisabledException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new CustomResponseMessage(false, ex.getMessage()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
    return new ResponseEntity<>(Map.of(
      "timestamp", LocalDateTime.now(),
      "message", ex.getMessage(),
      "status", HttpStatus.NOT_FOUND.value()
    ), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<?> handleInvalidRequest(InvalidRequestException ex, WebRequest request) {
    return new ResponseEntity<>(Map.of(
      "timestamp", LocalDateTime.now(),
      "message", ex.getMessage(),
      "status", HttpStatus.BAD_REQUEST.value()
    ), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CustomResponseMessage> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(new CustomResponseMessage(false, "Unexpected error: " + ex.getMessage()));
  }
}