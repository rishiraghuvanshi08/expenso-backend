package com.expenso.Expenso.enums.response;

public enum AuthResponseMessage {

  // Registration
  OTP_SENT("OTP sent to your email. Please verify to complete registration."),
  EMAIL_ALREADY_REGISTERED("Email already registered."),
  REGISTRATION_SUCCESS("Registration successful. Please log in."),
  REGISTRATION_FAILED("Registration failed."),

  // OTP
  INVALID_OR_EXPIRED_OTP("Invalid or expired OTP."),
  OTP_VERIFICATION_SUCCESS("OTP verified successfully."),
  OTP_VERIFICATION_FAILED("OTP verification failed."),

  // Login
  LOGIN_SUCCESS("Login successful."),
  LOGIN_FAILED("Invalid email or password."),
  USER_NOT_FOUND("User not found."),
  USER_DISABLED("User account is deactivated."),

  // Logout (for future use)
  LOGOUT_SUCCESS("Logout successful."),
  LOGOUT_FAILED("Logout failed."),

  // Token
  TOKEN_GENERATION_FAILED("Failed to generate authentication token."),
  TOKEN_VALIDATION_FAILED("Invalid or expired token.");

  private final String message;

  AuthResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}