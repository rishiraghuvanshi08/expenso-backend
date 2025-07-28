package com.expenso.Expenso.dto.usertransaction;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserTransactionUpdateDTO {

  @NotNull
  private Long categoryId;

  private String note;

  @NotNull
  private LocalDate date;
}