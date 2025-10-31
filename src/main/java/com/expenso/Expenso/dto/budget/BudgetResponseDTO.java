package com.expenso.Expenso.dto.budget;

import com.expenso.Expenso.enums.entity.BudgetStatus;
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
@Schema(description = "Response payload containing budget details and spending summary.")
public class BudgetResponseDTO {

  @Schema(description = "Unique identifier of the budget.", example = "10")
  private Long id;

  @Schema(description = "ID of the category this budget belongs to.", example = "5")
  private Long categoryId;

  @Schema(description = "Name of the associated category.", example = "Utilities")
  private String categoryName;

  @Schema(description = "Budgeted amount for the category.", example = "8000.00")
  private BigDecimal amount;

  @Schema(description = "Start date of the budget period.", example = "2025-11-01")
  private LocalDate startDate;

  @Schema(description = "End date of the budget period.", example = "2025-11-30")
  private LocalDate endDate;

  @Schema(description = "Indicates if the budget is currently active.", example = "true")
  private boolean active;

  @Schema(description = "Current status of the budget (ACTIVE, UPCOMING, EXPIRED).", example = "ACTIVE")
  private BudgetStatus status;

  @Schema(description = "Total amount spent so far under this budget.", example = "3560.25")
  private BigDecimal totalSpent;
}