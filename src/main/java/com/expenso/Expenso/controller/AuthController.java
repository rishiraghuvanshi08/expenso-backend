package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.auth.AuthResponse;
import com.expenso.Expenso.dto.auth.LoginRequest;
import com.expenso.Expenso.dto.auth.RegisterRequest;
import com.expenso.Expenso.enums.response.AuthResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/app-users/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthServiceImpl authService;

  @PostMapping("/register")
  public ResponseEntity<CustomResponseMessage> register(@RequestBody RegisterRequest request) {
    authService.initiateRegistration(request);
    return ResponseEntity.ok(new CustomResponseMessage(true, AuthResponseMessage.OTP_SENT.getMessage()));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<CustomResponseMessage> verifyOtp(@RequestParam String email, @RequestParam String otp) {
    authService.completeRegistration(email, otp);
    return ResponseEntity.ok(new CustomResponseMessage(true, AuthResponseMessage.REGISTRATION_SUCCESS.getMessage()));
  }

  @PostMapping("/login")
  public ResponseEntity<CustomResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(new CustomResponse<>(true, AuthResponseMessage.LOGIN_SUCCESS.getMessage(), response));
  }
}
