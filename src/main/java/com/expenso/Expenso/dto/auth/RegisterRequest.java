package com.expenso.Expenso.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User registration request data")
public class RegisterRequest {
  @Schema(description = "User's full name", example = "Ram Raghuvanshi")
  private String name;

  @Schema(description = "Valid email address of the user", example = "ramraghuvanshi@gmail.com")
  private String email;

  @Schema(description = "Password with minimum 8 characters", example = "12345678")
  private String password;

  @Schema(description = "Mobile number", example = "+919876543210")
  private String phoneNumber;

  public RegisterRequest() {
  }

  public RegisterRequest(String name, String email, String password, String phoneNumber) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
  }
}