package com.expenso.Expenso.controller;

import com.expenso.Expenso.enums.response.AppUserResponseMessage;
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

  @GetMapping
  public ResponseEntity<CustomResponse<AppUserResponseDTO>> getUser(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<AppUserResponseDTO>(true, AppUserResponseMessage.USER_FETCH_SUCCESS.getMessage(), appUserService.getUserById(userId)));
  }

  @PutMapping
  public ResponseEntity<CustomResponse<AppUserResponseDTO>> updateUser(@RequestAttribute("userId") Long userId, @Valid @RequestBody AppUserUpdateDTO dto) {
    return ResponseEntity.ok(new CustomResponse<AppUserResponseDTO>(true, AppUserResponseMessage.USER_UPDATE_SUCCESS.getMessage(), appUserService.updateUser(userId, dto)));
  }

  @DeleteMapping
  public ResponseEntity<CustomResponseMessage> deactivateUser(@RequestAttribute("userId") Long userId) {
    appUserService.deactivateUser(userId);
    return ResponseEntity.ok(new CustomResponseMessage(true, AppUserResponseMessage.USER_DEACTIVATION_SUCCESS.getMessage()));
  }

  @PutMapping("/change-password")
  public ResponseEntity<CustomResponseMessage> changePassword(@RequestAttribute("userId") Long userId, @Valid @RequestBody PasswordChangeDTO dto) {
    appUserService.changePassword(userId, dto);
    return ResponseEntity.ok(new CustomResponseMessage(true, AppUserResponseMessage.PASSWORD_CHANGE_SUCCESS.getMessage()));
  }
}