package com.expenso.Expenso.exception.custom;

public class InvalidOtpException extends RuntimeException {
  public InvalidOtpException(String message) {
    super(message);
  }
}