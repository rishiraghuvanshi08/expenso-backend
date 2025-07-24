package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.auth.AuthResponse;
import com.expenso.Expenso.dto.auth.LoginRequest;
import com.expenso.Expenso.dto.auth.RegisterRequest;

public interface AuthService {
  AuthResponse login(LoginRequest request);
  void initiateRegistration(RegisterRequest request);
  void completeRegistration(String email, String otp);
}
