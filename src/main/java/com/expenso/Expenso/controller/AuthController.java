package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.AuthResponse;
import com.expenso.Expenso.dto.LoginRequest;
import com.expenso.Expenso.dto.RegisterRequest;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/app-users/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<CustomResponseMessage> register(@RequestBody RegisterRequest request) {
    authService.initiateRegistration(request);
    return ResponseEntity.ok(new CustomResponseMessage(true, "OTP sent to your email. Please verify to complete registration."));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<CustomResponseMessage> verifyOtp(@RequestParam String email, @RequestParam String otp) {
    authService.completeRegistration(email, otp);
    return ResponseEntity.ok(new CustomResponseMessage(true, "Registration successful. Please log in."));
  }

  @PostMapping("/login")
  public ResponseEntity<CustomResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(new CustomResponse<>(true, "Login successful", response));
  }
}
