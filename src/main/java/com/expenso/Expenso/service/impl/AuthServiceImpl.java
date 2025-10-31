package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.dto.auth.AuthResponse;
import com.expenso.Expenso.dto.auth.LoginRequest;
import com.expenso.Expenso.dto.auth.RegisterRequest;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.enums.response.AuthResponseMessage;
import com.expenso.Expenso.exception.custom.EmailAlreadyExistsException;
import com.expenso.Expenso.exception.custom.InvalidOtpException;
import com.expenso.Expenso.exception.custom.UserDisabledException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.security.JwtService;
import com.expenso.Expenso.service.AuthService;
import com.expenso.Expenso.service.redis.TempUserStore;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of {@link AuthService}.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final TempUserStore tempUserStore;
  private final AppUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authManager;
  private final EmailServiceImpl emailService;

  /**
   * @see AuthService#initiateRegistration(RegisterRequest) 
   */
  @Override
  @Transactional
  public void initiateRegistration(RegisterRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(AuthResponseMessage.EMAIL_ALREADY_REGISTERED.getMessage());
    }

    String otp = tempUserStore.cacheUserRequest(request); // returns OTP
    emailService.sendOtpEmail(request.getEmail(), otp);
  }

  /**
   * @see AuthService#completeRegistration(String, String)
   */
  @Override
  @Transactional
  public void completeRegistration(String email, String otp) {
    if (!tempUserStore.verifyOtp(email, otp)) {
      throw new InvalidOtpException(AuthResponseMessage.INVALID_OR_EXPIRED_OTP.getMessage());
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

  /**
   * @see AuthService#login(LoginRequest)
   */
  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    authManager.authenticate(new UsernamePasswordAuthenticationToken(
      request.getEmail(), request.getPassword()));

    AppUser user = userRepository.findByEmail(request.getEmail())
                                 .orElseThrow(() -> new UsernameNotFoundException(AuthResponseMessage.USER_NOT_FOUND.getMessage()));

    if (!user.isActive()) {
      throw new UserDisabledException(AuthResponseMessage.USER_DISABLED.getMessage());
    }

    String token = jwtService.generateToken(user.getId(), user.getEmail());

    return AuthResponse.builder()
                       .token(token)
                       .build();
  }
}