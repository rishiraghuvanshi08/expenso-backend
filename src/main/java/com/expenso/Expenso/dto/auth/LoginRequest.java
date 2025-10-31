package com.expenso.Expenso.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login request containing user's credentials")
public class LoginRequest {
  @Schema(description = "Registered email of user", example = "user@gmail.com")
  private String email;

  @Schema(description = "User's password", example = "12345678")
  private String password;

  public LoginRequest() {
  }

  public LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }
}