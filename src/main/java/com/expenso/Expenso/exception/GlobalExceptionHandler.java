package com.expenso.Expenso.exception;

import com.expenso.Expenso.exception.custom.EmailAlreadyExistsException;
import com.expenso.Expenso.exception.custom.InvalidOtpException;
import com.expenso.Expenso.response.CustomResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CustomResponseMessage> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(new CustomResponseMessage(false, "Unexpected error: " + ex.getMessage()));
  }
}