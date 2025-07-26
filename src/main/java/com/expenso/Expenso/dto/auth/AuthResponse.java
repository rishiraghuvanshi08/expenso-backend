package com.expenso.Expenso.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
  private String token;

  public AuthResponse() {
  }

  public AuthResponse(String token) {
    this.token = token;
  }
}