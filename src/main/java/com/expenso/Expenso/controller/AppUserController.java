package com.expenso.Expenso.controller;

import com.expenso.Expenso.enums.response.AppUserResponseMessage;
import com.expenso.Expenso.dto.appuser.AppUserResponseDTO;
import com.expenso.Expenso.dto.appuser.AppUserUpdateDTO;
import com.expenso.Expenso.dto.appuser.PasswordChangeDTO;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that manages operations related to the authenticated user's profile,
 * including fetching user details, updating profile information, deactivating the
 * account, and changing the password.
 *
 * Base URL: /api/v1/app-users
 *
 * All endpoints in this controller require a valid JWT token and use the userId
 * set in the request attributes after successful authentication.
 */
@RestController
@RequestMapping("/api/v1/app-users")
@RequiredArgsConstructor
@Tag(name = "App User APIs", description = "Handles user profile management operations like fetching user details, updating profile, deactivation, and password change")
public class AppUserController {

  private final AppUserService appUserService;

  /**
   * Fetches user details of the currently authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter)
   * @return ResponseEntity containing user details
   */
  @Operation(
    summary = "Get logged-in user details",
    description = "Fetches profile information of the authenticated user using the extracted userId from JWT."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User fetched successfully"),
    @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping
  public ResponseEntity<CustomResponse<AppUserResponseDTO>> getUser(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<AppUserResponseDTO>(true, AppUserResponseMessage.USER_FETCH_SUCCESS.getMessage(), appUserService.getUserById(userId)));
  }

  /**
   * Updates user profile values like name and phone number.
   *
   * @param userId ID of the logged-in user (injected via JWT filter)
   * @param dto    Data containing updatable fields
   * @return Updated user details
   */
  @Operation(
    summary = "Update user profile",
    description = "Allows updating profile fields like name and phone number for the authenticated user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User updated successfully"),
    @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PutMapping
  public ResponseEntity<CustomResponse<AppUserResponseDTO>> updateUser(@RequestAttribute("userId") Long userId, @Valid @RequestBody AppUserUpdateDTO dto) {
    return ResponseEntity.ok(new CustomResponse<AppUserResponseDTO>(true, AppUserResponseMessage.USER_UPDATE_SUCCESS.getMessage(), appUserService.updateUser(userId, dto)));
  }

  /**
   * Deactivates the current user by marking the account as inactive.
   * Also masks the email and resets password for security.
   *
   * @param userId User ID from JWT (injected via JWT filter)
   */
  @Operation(
    summary = "Deactivate user account",
    description = "Soft deletes the user by marking the account as inactive, masking email and removing access."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
    @ApiResponse(responseCode = "404", description = "User not found"),
    @ApiResponse(responseCode = "409", description = "User already deactivated")
  })
  @DeleteMapping
  public ResponseEntity<CustomResponseMessage> deactivateUser(@RequestAttribute("userId") Long userId) {
    appUserService.deactivateUser(userId);
    return ResponseEntity.ok(new CustomResponseMessage(true, AppUserResponseMessage.USER_DEACTIVATION_SUCCESS.getMessage()));
  }

  /**
   * Allows user to change their password by verifying old password.
   *
   * @param userId Authenticated user ID (injected via JWT filter)
   * @param dto    Contains old and new passwords
   */
  @Operation(
    summary = "Change account password",
    description = "Validates old password and updates it to a new encoded password for the user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
    @ApiResponse(responseCode = "400", description = "Old password is incorrect"),
    @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PutMapping("/change-password")
  public ResponseEntity<CustomResponseMessage> changePassword(@RequestAttribute("userId") Long userId, @Valid @RequestBody PasswordChangeDTO dto) {
    appUserService.changePassword(userId, dto);
    return ResponseEntity.ok(new CustomResponseMessage(true, AppUserResponseMessage.PASSWORD_CHANGE_SUCCESS.getMessage()));
  }
}