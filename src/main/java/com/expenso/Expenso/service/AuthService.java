package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.auth.AuthResponse;
import com.expenso.Expenso.dto.auth.LoginRequest;
import com.expenso.Expenso.dto.auth.RegisterRequest;

/**
 * Handles authentication operations like registration, OTP verification, and login.
 */
public interface AuthService {

  /**
   * Authenticates user using email and password and generates JWT.
   *
   * @param request login credentials.
   * @return AuthResponse containing JWT token.
   * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if user does not exist.
   * @throws com.expenso.Expenso.exception.custom.UserDisabledException if user is disabled
   */
  AuthResponse login(LoginRequest request);

  /**
   * Starts user registration by storing user temporarily and emailing OTP.
   *
   * @param request registration data.
   * @throws com.expenso.Expenso.exception.custom.EmailAlreadyExistsException if email is already registered.
   */
  void initiateRegistration(RegisterRequest request);

  /**
   * Completes user registration after OTP verification.
   *
   * @param email user's email.
   * @param otp   one-time password.
   * @throws com.expenso.Expenso.exception.custom.InvalidOtpException if OTP is invalid or expired.
   */
  void completeRegistration(String email, String otp);
}
