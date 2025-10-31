package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.appuser.AppUserResponseDTO;
import com.expenso.Expenso.dto.appuser.AppUserUpdateDTO;
import com.expenso.Expenso.dto.appuser.PasswordChangeDTO;

/**
 * Service interface for managing AppUser accounts.
 * Provides methods for fetching profile details, updating user info,
 * deactivating accounts, and updating passwords.
 */
public interface AppUserService {

  /**
   * Fetches user details by ID.
   *
   * @param id Unique ID of the user.
   * @return User details in DTO format.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if user does not exist.
   */
  AppUserResponseDTO getUserById(Long id);

  /**
   * Updates user profile information (name, phone number).
   * Only non-null fields in the DTO are updated.
   *
   * @param id  ID of the user to update.
   * @param dto Contains update values.
   * @return Updated user details.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if user is not found.
   */
  AppUserResponseDTO updateUser(Long id, AppUserUpdateDTO dto);

  /**
   * Deactivates a user account by marking it inactive,
   * masking email, and invalidating credentials.
   *
   * @param id ID of the user to deactivate.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if user does not exist.
   * @throws com.expenso.Expenso.exception.custom.UserAlreadyDeactivatedException if user is already inactive.
   */
  void deactivateUser(Long id);

  /**
   * Changes user password after verifying the old password.
   *
   * @param id  ID of the logged-in user.
   * @param dto Contains old password and new password.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if user does not exist.
   * @throws com.expenso.Expenso.exception.custom.InvalidRequestException if old password is incorrect.
   */
  void changePassword(Long id, PasswordChangeDTO dto);
}