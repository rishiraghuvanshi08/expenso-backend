package com.expenso.Expenso.service;

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
import com.expenso.Expenso.service.impl.AuthServiceImpl;
import com.expenso.Expenso.service.impl.EmailServiceImpl;
import com.expenso.Expenso.service.redis.TempUserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

  @Mock private TempUserStore tempUserStore;
  @Mock private AppUserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private AuthenticationManager authManager;
  @Mock private EmailServiceImpl emailService;

  @InjectMocks private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // ===== initiateRegistration() Tests =====

  @Test
  void initiateRegistration_shouldSendOtp_whenEmailNotRegistered() {
    RegisterRequest request = new RegisterRequest("John", "john@example.com", "password", "1234567890");

    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(tempUserStore.cacheUserRequest(request)).thenReturn("123456");

    authService.initiateRegistration(request);

    verify(emailService).sendOtpEmail("john@example.com", "123456");
  }

  @Test
  void initiateRegistration_shouldThrow_whenEmailAlreadyExists() {
    RegisterRequest request = new RegisterRequest("John", "john@example.com", "password", "1234567890");

    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mock(AppUser.class)));

    assertThatThrownBy(() -> authService.initiateRegistration(request))
      .isInstanceOf(EmailAlreadyExistsException.class)
      .hasMessageContaining(AuthResponseMessage.EMAIL_ALREADY_REGISTERED.getMessage());
  }

  // ===== completeRegistration() Tests =====

  @Test
  void completeRegistration_shouldSaveUser_whenOtpValid() {
    String email = "john@example.com";
    String otp = "123456";

    RegisterRequest request = new RegisterRequest("John", email, "rawPass", "1234567890");

    when(tempUserStore.verifyOtp(email, otp)).thenReturn(true);
    when(tempUserStore.getRequest(email)).thenReturn(request);
    when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");

    authService.completeRegistration(email, otp);

    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    verify(userRepository).save(userCaptor.capture());

    AppUser savedUser = userCaptor.getValue();
    assertThat(savedUser.getEmail()).isEqualTo(email);
    assertThat(savedUser.getPassword()).isEqualTo("encodedPass");
    assertThat(savedUser.getName()).isEqualTo("John");

    verify(tempUserStore).clear(email);
  }

  @Test
  void completeRegistration_shouldThrow_whenOtpInvalid() {
    when(tempUserStore.verifyOtp("invalid@example.com", "wrongOtp")).thenReturn(false);

    assertThatThrownBy(() -> authService.completeRegistration("invalid@example.com", "wrongOtp"))
      .isInstanceOf(InvalidOtpException.class)
      .hasMessageContaining(AuthResponseMessage.INVALID_OR_EXPIRED_OTP.getMessage());
  }

  // ===== login() Tests =====

  @Test
  void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
    LoginRequest request = new LoginRequest("john@example.com", "password");

    AppUser user = AppUser.builder()
                          .id(1L)
                          .email("john@example.com")
                          .build();

    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
    when(jwtService.generateToken("john@example.com")).thenReturn("jwt-token");

    AuthResponse response = authService.login(request);

    assertThat(response).isNotNull();
    assertThat(response.getEmail()).isEqualTo("john@example.com");
    assertThat(response.getToken()).isEqualTo("jwt-token");
    assertThat(response.getUserId()).isEqualTo(1L);
  }

  @Test
  void login_shouldThrow_whenUserNotFound() {
    LoginRequest request = new LoginRequest("unknown@example.com","pass");

    // Return a dummy Authentication token (required return type)
    Authentication dummyAuth = new UsernamePasswordAuthenticationToken(
      request.getEmail(), request.getPassword()
    );
    when(authManager.authenticate(any())).thenReturn(dummyAuth);

    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request))
      .isInstanceOf(UsernameNotFoundException.class)
      .hasMessageContaining(AuthResponseMessage.USER_NOT_FOUND.getMessage());
  }

  @Test
  void login_shouldThrow_whenUserIsDisabled() {
    LoginRequest request = new LoginRequest("disabled@example.com", "password");

    AppUser disabledUser = AppUser.builder()
                                  .id(2L)
                                  .email("disabled@example.com")
                                  .isActive(false)
                                  .build();

    // Simulate successful auth but user is disabled
    Authentication dummyAuth = new UsernamePasswordAuthenticationToken(
      request.getEmail(), request.getPassword()
    );
    when(authManager.authenticate(any())).thenReturn(dummyAuth);
    when(userRepository.findByEmail("disabled@example.com")).thenReturn(Optional.of(disabledUser));

    assertThatThrownBy(() -> authService.login(request))
      .isInstanceOf(UserDisabledException.class)
      .hasMessageContaining(AuthResponseMessage.USER_DISABLED.getMessage());
  }
}