package com.expenso.Expenso.exception.custom;

public class UserAlreadyDeactivatedException extends RuntimeException {
  public UserAlreadyDeactivatedException(String message) {
    super(message);
  }
}
