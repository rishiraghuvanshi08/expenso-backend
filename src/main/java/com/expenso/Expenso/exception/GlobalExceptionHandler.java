package com.expenso.Expenso.exception;

import com.expenso.Expenso.exception.custom.*;
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

  @ExceptionHandler(UserAlreadyDeactivatedException.class)
  public ResponseEntity<CustomResponseMessage> handleUserAlreadyDisabled(UserAlreadyDeactivatedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
                         .body(new CustomResponseMessage(false, ex.getMessage()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomResponseMessage> handleResourceNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                         .body(new CustomResponseMessage(false, ex.getMessage()));
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<CustomResponseMessage> handleInvalidRequest(InvalidRequestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new CustomResponseMessage(false, ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CustomResponseMessage> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(new CustomResponseMessage(false, "Unexpected error: " + ex.getMessage()));
  }
}