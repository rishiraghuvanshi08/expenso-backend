package com.expenso.Expenso.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Standard response wrapper for API messages without data payload.
 */
@Getter
@Setter
public class CustomResponseMessage {
  private boolean status;
  private String message;

  public CustomResponseMessage() {
  }

  public CustomResponseMessage(boolean status, String message) {
    this.status = status;
    this.message = message;
  }

}