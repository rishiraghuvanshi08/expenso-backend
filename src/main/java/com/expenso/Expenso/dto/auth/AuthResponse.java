package com.expenso.Expenso.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
  private Long userId;
  private String email;
  private String token;

  public AuthResponse() {
  }

  public AuthResponse(Long userId, String email, String token) {
    this.userId = userId;
    this.email = email;
    this.token = token;
  }
}