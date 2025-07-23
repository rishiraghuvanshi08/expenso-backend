package com.expenso.Expenso.exception.custom;

public class UserDisabledException extends RuntimeException {
  public UserDisabledException(String message) {
    super(message);
  }
}
