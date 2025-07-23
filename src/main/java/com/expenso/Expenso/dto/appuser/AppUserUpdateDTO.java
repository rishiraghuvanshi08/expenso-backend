package com.expenso.Expenso.dto.appuser;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserUpdateDTO {

  private String name;

  @Size(max = 15)
  private String phoneNumber;
}