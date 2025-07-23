package com.expenso.Expenso.dto.appuser;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserResponseDTO {
  private Long id;
  private String name;
  private String email;
  private String phoneNumber;
  private LocalDateTime createdAt;
}