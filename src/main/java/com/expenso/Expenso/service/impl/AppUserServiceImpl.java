package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.constants.response.AppUserResponseMessage;
import com.expenso.Expenso.dto.appuser.AppUserResponseDTO;
import com.expenso.Expenso.dto.appuser.AppUserUpdateDTO;
import com.expenso.Expenso.dto.appuser.PasswordChangeDTO;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.exception.custom.InvalidRequestException;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.exception.custom.UserAlreadyDeactivatedException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.service.AppUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public AppUserResponseDTO getUserById(Long id) {
    AppUser user = findUserOrThrow(id);
    return mapToDTO(user);
  }

  @Override
  @Transactional
  public AppUserResponseDTO updateUser(Long id, AppUserUpdateDTO dto) {
    AppUser user = findUserOrThrow(id);

    if (dto.getName() != null) {
      user.setName(dto.getName());
    }
    if (dto.getPhoneNumber() != null) {
      user.setPhoneNumber(dto.getPhoneNumber());
    }
    return mapToDTO(appUserRepository.save(user));
  }

  @Override
  @Transactional
  public void deactivateUser(Long id) {
    AppUser user = findUserOrThrow(id);

    // If already deactivated, optionally throw or just return
    if (!user.isActive()) {
      throw new UserAlreadyDeactivatedException(AppUserResponseMessage.USER_ALREADY_DEACTIVATED.getMessage());
    }

    // Mark user as inactive
    user.setActive(false);

    user.setEmail(user.getEmail() + ".deactivated." + System.currentTimeMillis()); // Mask email
    user.setPassword("deactivated");
    appUserRepository.save(user);
  }

  @Override
  @Transactional
  public void changePassword(Long id, PasswordChangeDTO dto) {
    AppUser user = findUserOrThrow(id);

    if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
      throw new InvalidRequestException(AppUserResponseMessage.PASSWORD_MISMATCH.getMessage());
    }

    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    appUserRepository.save(user);
  }

  private AppUser findUserOrThrow(Long id) {
    return appUserRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(AppUserResponseMessage.USER_NOT_FOUND.getMessage()));
  }

  private AppUserResponseDTO mapToDTO(AppUser user) {
    return AppUserResponseDTO.builder()
                             .id(user.getId())
                             .name(user.getName())
                             .email(user.getEmail())
                             .phoneNumber(user.getPhoneNumber())
                             .createdAt(user.getCreatedAt())
                             .build();
  }
}