package com.expenso.Expenso.dto.appuser;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating user profile details")
public class AppUserUpdateDTO {

  @Schema(description = "Updated full name of the user", example = "Krishna Raghuvanshi")
  private String name;

  @Size(max = 15)
  @Schema(description = "Updated phone number", example = "1234567890", maxLength = 15)
  private String phoneNumber;
}