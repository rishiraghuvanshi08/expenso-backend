package com.expenso.Expenso.controller;

import com.expenso.Expenso.constants.response.AppUserResponseMessage;
import com.expenso.Expenso.dto.appuser.AppUserResponseDTO;
import com.expenso.Expenso.dto.appuser.AppUserUpdateDTO;
import com.expenso.Expenso.dto.appuser.PasswordChangeDTO;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/app-users")
@RequiredArgsConstructor
public class AppUserController {

  private final AppUserService appUserService;

  @GetMapping("/{id}")
  public ResponseEntity<CustomResponse<AppUserResponseDTO>> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(new CustomResponse<AppUserResponseDTO>(true, AppUserResponseMessage.USER_FETCH_SUCCESS.getMessage(), appUserService.getUserById(id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomResponse<AppUserResponseDTO>> updateUser(@PathVariable Long id,
                                                       @Valid @RequestBody AppUserUpdateDTO dto) {
    return ResponseEntity.ok(new CustomResponse<AppUserResponseDTO>(true, AppUserResponseMessage.USER_UPDATE_SUCCESS.getMessage(), appUserService.updateUser(id, dto)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<CustomResponseMessage> deactivateUser(@PathVariable Long id) {
    appUserService.deactivateUser(id);
    return ResponseEntity.ok(new CustomResponseMessage(true, AppUserResponseMessage.USER_DEACTIVATION_SUCCESS.getMessage()));
  }

  @PutMapping("/{userId}/change-password")
  public ResponseEntity<CustomResponseMessage> changePassword(@PathVariable Long userId,
                                             @Valid @RequestBody PasswordChangeDTO dto) {
    appUserService.changePassword(userId, dto);
    return ResponseEntity.ok(new CustomResponseMessage(true, AppUserResponseMessage.PASSWORD_CHANGE_SUCCESS.getMessage()));
  }
}