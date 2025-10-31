package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.auth.AuthResponse;
import com.expenso.Expenso.dto.auth.LoginRequest;
import com.expenso.Expenso.dto.auth.RegisterRequest;
import com.expenso.Expenso.enums.response.AuthResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling all authentication-related operations such as
 * user registration, OTP verification, and login.
 *
 * Base URL: /api/v1/app-users/auth
 */
@RestController
@RequestMapping("/api/v1/app-users/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication APIs", description = "Handles user registration, OTP verification, and login")
public class AuthController {

  private final AuthServiceImpl authService;

  /**
   * Initiates the user registration process.
   *
   * @param request the complete registration details of the user.
   * @return CustomResponseMessage indicating OTP dispatch status.
   */
  @Operation(
    summary = "Register a new user (Step 1)",
    description = "Accepts user registration data and sends OTP to email for verification."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OTP sent to user's email"),
    @ApiResponse(responseCode = "409", description = "Email already registered"),
  })
  @PostMapping("/register")
  public ResponseEntity<CustomResponseMessage> register(@RequestBody RegisterRequest request) {
    authService.initiateRegistration(request);
    return ResponseEntity.ok(new CustomResponseMessage(true, AuthResponseMessage.OTP_SENT.getMessage()));
  }

  /**
   * Verifies OTP and completes user registration.
   *
   * @param email registered email of the user.
   * @param otp   the one-time password received by the user.
   * @return CustomResponseMessage confirming successful registration.
   */
  @Operation(
    summary = "Verify OTP and complete registration",
    description = "Confirms OTP and creates a new user account in the system."
  )
  @Parameters({
    @Parameter(name = "email", description = "User's registered email", required = true),
    @Parameter(name = "otp", description = "One-time password sent to email", required = true)
  })
  @PostMapping("/verify-otp")
  public ResponseEntity<CustomResponseMessage> verifyOtp(@RequestParam String email, @RequestParam String otp) {
    authService.completeRegistration(email, otp);
    return ResponseEntity.ok(new CustomResponseMessage(true, AuthResponseMessage.REGISTRATION_SUCCESS.getMessage()));
  }

  /**
   * Authenticates a user and returns a JWT token upon successful login.
   *
   * @param request contains email and password fields for authentication.
   * @return CustomResponse containing JWT token and a success message.
   */
  @Operation(
    summary = "Login with email and password",
    description = "Authenticates the user and returns a JWT token upon success."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
  })
  @PostMapping("/login")
  public ResponseEntity<CustomResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(new CustomResponse<>(true, AuthResponseMessage.LOGIN_SUCCESS.getMessage(), response));
  }
}
