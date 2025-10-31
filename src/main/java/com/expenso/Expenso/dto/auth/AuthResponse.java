package com.expenso.Expenso.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Contains JWT token after successful login or registration")
public class AuthResponse {

  @Schema(description = "JWT access token for authorization", example = "eyJhbGciOiJIUzI1NiIsIn...")
  private String token;

  public AuthResponse() {
  }

  public AuthResponse(String token) {
    this.token = token;
  }
}