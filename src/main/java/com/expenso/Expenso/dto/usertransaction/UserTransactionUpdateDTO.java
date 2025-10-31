package com.expenso.Expenso.dto.usertransaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request object for updating an existing user transaction.")
public class UserTransactionUpdateDTO {

  @NotNull
  @Schema(description = "Updated category ID for the transaction.", example = "2")
  private Long categoryId;

  @Schema(description = "Updated note or description for the transaction.", example = "Dinner at restaurant")
  private String note;

  @NotNull
  @Schema(description = "Updated date for the transaction.", example = "2025-10-25")
  private LocalDate date;
}