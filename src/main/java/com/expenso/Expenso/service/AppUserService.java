package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.appuser.AppUserResponseDTO;
import com.expenso.Expenso.dto.appuser.AppUserUpdateDTO;
import com.expenso.Expenso.dto.appuser.PasswordChangeDTO;

public interface AppUserService {
  AppUserResponseDTO getUserById(Long id);
  AppUserResponseDTO updateUser(Long id, AppUserUpdateDTO dto);
  void deactivateUser(Long id);
  void changePassword(Long id, PasswordChangeDTO dto);
}