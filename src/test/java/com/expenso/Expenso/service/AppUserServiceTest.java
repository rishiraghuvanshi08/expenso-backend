package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.appuser.AppUserResponseDTO;
import com.expenso.Expenso.dto.appuser.AppUserUpdateDTO;
import com.expenso.Expenso.dto.appuser.PasswordChangeDTO;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.enums.response.AppUserResponseMessage;
import com.expenso.Expenso.exception.custom.InvalidRequestException;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.exception.custom.UserAlreadyDeactivatedException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.service.impl.AppUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserServiceTest {

  @Mock
  private AppUserRepository appUserRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AppUserServiceImpl appUserService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private AppUser buildUser() {
    return AppUser.builder()
                  .id(1L)
                  .name("Rishi")
                  .email("rishi@gmail.com")
                  .phoneNumber("9876543210")
                  .password("encodedOldPassword")
                  .isActive(true)
                  .createdAt(LocalDateTime.now())
                  .build();
  }

  // ================= getUserById() =================

  @Test
  void getUserById_shouldReturnUser_whenUserExists() {
    AppUser user = buildUser();

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

    AppUserResponseDTO response = appUserService.getUserById(1L);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getName()).isEqualTo("Rishi");
    assertThat(response.getEmail()).isEqualTo("rishi@gmail.com");
  }

  @Test
  void getUserById_shouldThrow_whenUserNotFound() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> appUserService.getUserById(1L))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(AppUserResponseMessage.USER_NOT_FOUND.getMessage());
  }

  // ================= updateUser() =================

  @Test
  void updateUser_shouldUpdateNameAndPhone_whenValidRequest() {
    AppUser user = buildUser();

    AppUserUpdateDTO dto = new AppUserUpdateDTO();
    dto.setName("Updated Rishi");
    dto.setPhoneNumber("9999999999");

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

    AppUserResponseDTO response = appUserService.updateUser(1L, dto);

    assertThat(response.getName()).isEqualTo("Updated Rishi");
    assertThat(response.getPhoneNumber()).isEqualTo("9999999999");

    verify(appUserRepository).save(user);
  }

  @Test
  void updateUser_shouldThrow_whenUserNotFound() {
    AppUserUpdateDTO dto = new AppUserUpdateDTO();

    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> appUserService.updateUser(1L, dto))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(AppUserResponseMessage.USER_NOT_FOUND.getMessage());
  }

  // ================= deactivateUser() =================

  @Test
  void deactivateUser_shouldDeactivateUser_whenActive() {
    AppUser user = buildUser();

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

    appUserService.deactivateUser(1L);

    assertThat(user.isActive()).isFalse();
    assertThat(user.getPassword()).isEqualTo("deactivated");
    assertThat(user.getEmail()).contains(".deactivated.");

    verify(appUserRepository).save(user);
  }

  @Test
  void deactivateUser_shouldThrow_whenAlreadyDeactivated() {
    AppUser user = buildUser();
    user.setActive(false);

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> appUserService.deactivateUser(1L))
      .isInstanceOf(UserAlreadyDeactivatedException.class)
      .hasMessageContaining(AppUserResponseMessage.USER_ALREADY_DEACTIVATED.getMessage());
  }

  @Test
  void deactivateUser_shouldThrow_whenUserNotFound() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> appUserService.deactivateUser(1L))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(AppUserResponseMessage.USER_NOT_FOUND.getMessage());
  }

  // ================= changePassword() =================

  @Test
  void changePassword_shouldUpdatePassword_whenOldPasswordMatches() {
    AppUser user = buildUser();

    PasswordChangeDTO dto = new PasswordChangeDTO();
    dto.setOldPassword("oldPass");
    dto.setNewPassword("newPass");

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);
    when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPassword");

    appUserService.changePassword(1L, dto);

    assertThat(user.getPassword()).isEqualTo("encodedNewPassword");

    verify(appUserRepository).save(user);
  }

  @Test
  void changePassword_shouldThrow_whenOldPasswordMismatch() {
    AppUser user = buildUser();

    PasswordChangeDTO dto = new PasswordChangeDTO();
    dto.setOldPassword("wrongPass");
    dto.setNewPassword("newPass");

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongPass", "encodedOldPassword")).thenReturn(false);

    assertThatThrownBy(() -> appUserService.changePassword(1L, dto))
      .isInstanceOf(InvalidRequestException.class)
      .hasMessageContaining(AppUserResponseMessage.PASSWORD_MISMATCH.getMessage());
  }

  @Test
  void changePassword_shouldThrow_whenUserNotFound() {
    PasswordChangeDTO dto = new PasswordChangeDTO();

    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> appUserService.changePassword(1L, dto))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(AppUserResponseMessage.USER_NOT_FOUND.getMessage());
  }
}