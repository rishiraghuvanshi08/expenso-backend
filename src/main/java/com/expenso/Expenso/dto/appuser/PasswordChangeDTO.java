package com.expenso.Expenso.dto.appuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO {

  @NotBlank
  private String oldPassword;

  @NotBlank
  @Size(min = 8)
  private String newPassword;
}