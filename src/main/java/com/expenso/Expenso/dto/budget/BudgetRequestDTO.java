package com.expenso.Expenso.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating or updating a personal budget.")
public class BudgetRequestDTO {

  @Schema(description = "ID of the expense category for which the budget is created.", example = "5")
  private Long categoryId;

  @Schema(description = "Total amount allocated for the budget.", example = "12000.00")
  private BigDecimal amount;

  @Schema(description = "Start date of the budget period.", example = "2025-10-01")
  private LocalDate startDate;

  @Schema(description = "End date of the budget period.", example = "2025-10-31", required = true)
  private LocalDate endDate;
}