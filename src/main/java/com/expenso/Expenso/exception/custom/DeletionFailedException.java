package com.expenso.Expenso.exception.custom;

public class DeletionFailedException extends RuntimeException {
  public DeletionFailedException(String message) {
    super(message);
  }
}
