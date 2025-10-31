package com.expenso.Expenso.dto.appuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "User details returned in API responses")
public class AppUserResponseDTO {

  @Schema(description = "Unique user ID", example = "101")
  private Long id;

  @Schema(description = "Full name of the user", example = "Ram Raghuvanshi")
  private String name;

  @Schema(description = "Registered email address", example = "ramraghuvanshi@gmail.com")
  private String email;

  @Schema(description = "User's contact number", example = "9876543210")
  private String phoneNumber;

  @Schema(description = "User account creation timestamp", example = "2024-07-12T10:15:30")
  private LocalDateTime createdAt;
}