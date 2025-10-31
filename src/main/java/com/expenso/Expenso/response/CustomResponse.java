package com.expenso.Expenso.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic response wrapper for API responses that include data.
 *
 * @param <T> the type of the response data
 */
@Getter
@Setter
public class CustomResponse<T> {
  private boolean status;
  private String message;
  private T data;

  public CustomResponse() {
  }

  public CustomResponse(boolean status, String message, T data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

}
