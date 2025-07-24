package com.expenso.Expenso.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
  private String name;
  private String email;
  private String password;
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