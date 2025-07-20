package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.*;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.exception.custom.EmailAlreadyExistsException;
import com.expenso.Expenso.exception.custom.InvalidOtpException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.security.JwtService;
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
  @Mock private EmailService emailService;

  @InjectMocks private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // ===== initiateRegistration() Tests =====

  @Test
  void initiateRegistration_shouldSendOtp_whenEmailNotRegistered() {
    RegisterRequest request = new RegisterRequest();
    request.setName("John");
    request.setEmail("john@example.com");
    request.setPassword("password");
    request.setPhoneNumber("1234567890");

    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(tempUserStore.cacheUserRequest(request)).thenReturn("123456");

    authService.initiateRegistration(request);

    verify(emailService).sendOtpEmail("john@example.com", "123456");
  }

  @Test
  void initiateRegistration_shouldThrow_whenEmailAlreadyExists() {
    RegisterRequest request = new RegisterRequest();
    request.setName("John");
    request.setEmail("john@example.com");
    request.setPassword("password");
    request.setPhoneNumber("1234567890");

    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mock(AppUser.class)));

    assertThatThrownBy(() -> authService.initiateRegistration(request))
      .isInstanceOf(EmailAlreadyExistsException.class)
      .hasMessageContaining("Email already registered");
  }

  // ===== completeRegistration() Tests =====

  @Test
  void completeRegistration_shouldSaveUser_whenOtpValid() {
    String email = "john@example.com";
    String otp = "123456";

    RegisterRequest request = new RegisterRequest();
    request.setName("John");
    request.setEmail(email);
    request.setPassword("rawPass");
    request.setPhoneNumber("1234567890");

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
      .hasMessageContaining("Invalid or expired OTP");
  }

  // ===== login() Tests =====

  @Test
  void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
    LoginRequest request = new LoginRequest();
    request.setEmail("john@example.com");
    request.setPassword("password");

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
    LoginRequest request = new LoginRequest();
    request.setEmail("unknown@example.com");
    request.setPassword("pass");

    // Return a dummy Authentication token (required return type)
    Authentication dummyAuth = new UsernamePasswordAuthenticationToken(
      request.getEmail(), request.getPassword()
    );
    when(authManager.authenticate(any())).thenReturn(dummyAuth);

    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request))
      .isInstanceOf(UsernameNotFoundException.class)
      .hasMessageContaining("User not found");
  }
}