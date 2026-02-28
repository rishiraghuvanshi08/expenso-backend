package com.expenso.Expenso.exception;

import com.expenso.Expenso.exception.custom.*;
import com.expenso.Expenso.response.CustomResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler for Expenso application.
 *
 * Centralizes exception handling across all controllers,
 * ensuring uniform response structure and HTTP status mapping.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<CustomResponseMessage> buildResponse(HttpStatus status, Exception ex) {
    return ResponseEntity.status(status).body(new CustomResponseMessage(false, ex.getMessage()));
  }

  /** Handles email duplication errors. */
  @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<CustomResponseMessage> handleEmailExists(EmailAlreadyExistsException ex) {
      return buildResponse(HttpStatus.CONFLICT, ex);
  }

  /** Handles invalid OTP scenarios. */
  @ExceptionHandler(InvalidOtpException.class)
  public ResponseEntity<CustomResponseMessage> handleInvalidOtp(InvalidOtpException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex);
  }

  /** Handles disabled user access attempts. */
  @ExceptionHandler(UserDisabledException.class)
  public ResponseEntity<CustomResponseMessage> handleUserDisabled(UserDisabledException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex);
  }

  /** Handles already deactivated user scenarios. */
  @ExceptionHandler(UserAlreadyDeactivatedException.class)
  public ResponseEntity<CustomResponseMessage> handleUserAlreadyDeactivated(UserAlreadyDeactivatedException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex);
  }

  /** Handles forbidden access errors. */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<CustomResponseMessage> handleAccessDenied(AccessDeniedException ex) {
    return buildResponse(HttpStatus.FORBIDDEN, ex);
  }

  /** Handles deletion failure cases. */
  @ExceptionHandler(DeletionFailedException.class)
  public ResponseEntity<CustomResponseMessage> handleDeletionFailed(DeletionFailedException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex);
  }

  /** Handles invalid combination scenarios. */
  @ExceptionHandler(InvalidCombinationException.class)
  public ResponseEntity<CustomResponseMessage> handleInvalidCombination(InvalidCombinationException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex);
  }

  /** Handles resource already exists errors. */
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<CustomResponseMessage> handleResourceExists(ResourceAlreadyExistsException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex);
  }

  /** Handles resource not found errors. */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomResponseMessage> handleResourceNotFound(ResourceNotFoundException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex);
  }

  /** Handles invalid request errors. */
  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<CustomResponseMessage> handleInvalidRequest(InvalidRequestException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex);
  }

  /** Handles illegal argument or state errors. */
  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
  public ResponseEntity<CustomResponseMessage> handleIllegalExceptions(RuntimeException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex);
  }

  /** Handles all unexpected runtime exceptions. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<CustomResponseMessage> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(new CustomResponseMessage(false, "Unexpected error: " + ex.getMessage()));
  }
}