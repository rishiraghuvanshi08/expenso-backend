package com.expenso.Expenso.dto.appuser;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for changing user password")
public class PasswordChangeDTO {

  @NotBlank
  @Schema(description = "Current password of the user", example = "oldPass123")
  private String oldPassword;

  @NotBlank
  @Size(min = 8)
  @Schema(description = "New password (min 8 characters)", example = "newPass123")
  private String newPassword;
}