package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.AuthResponse;
import com.expenso.Expenso.dto.LoginRequest;
import com.expenso.Expenso.dto.RegisterRequest;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.exception.custom.EmailAlreadyExistsException;
import com.expenso.Expenso.exception.custom.InvalidOtpException;
import com.expenso.Expenso.exception.custom.UserDisabledException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.security.JwtService;
import com.expenso.Expenso.service.redis.TempUserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final TempUserStore tempUserStore;
  private final AppUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authManager;
  private final EmailService emailService;

  public void initiateRegistration(RegisterRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException("Email already registered");
    }

    String otp = tempUserStore.cacheUserRequest(request); // returns OTP
    emailService.sendOtpEmail(request.getEmail(), otp);
  }

  public void completeRegistration(String email, String otp) {
    if (!tempUserStore.verifyOtp(email, otp)) {
      throw new InvalidOtpException("Invalid or expired OTP");
    }

    RegisterRequest req = tempUserStore.getRequest(email);
    AppUser user = AppUser.builder()
                          .name(req.getName())
                          .email(req.getEmail())
                          .password(passwordEncoder.encode(req.getPassword()))
                          .phoneNumber(req.getPhoneNumber())
                          .createdAt(LocalDateTime.now())
                          .build();

    userRepository.save(user);
    tempUserStore.clear(email); // Remove from Redis
  }

  public AuthResponse login(LoginRequest request) {
    authManager.authenticate(new UsernamePasswordAuthenticationToken(
      request.getEmail(), request.getPassword()));

    AppUser user = userRepository.findByEmail(request.getEmail())
                                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!user.isActive()) {
      throw new UserDisabledException("User account is deactivated");
    }

    String token = jwtService.generateToken(user.getEmail());

    return AuthResponse.builder()
                       .userId(user.getId())
                       .email(user.getEmail())
                       .token(token)
                       .build();
  }
}