package com.expenso.Expenso.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Analytical summary of budget usage including category details, spent amount, and utilization percentage.")
public class BudgetAnalyticsDTO {

  @Schema(description = "Name of the category under which the budget was created.", example = "Groceries")
  private String categoryName;

  @Schema(description = "Total amount allocated for this budget.", example = "15000.00")
  private BigDecimal budgetedAmount;

  @Schema(description = "Actual amount spent in this category during the budget period.", example = "12750.50")
  private BigDecimal actualSpent;

  @Schema(description = "Remaining budget amount.", example = "2249.50")
  private BigDecimal remaining;

  @Schema(description = "Percentage of the budget already used.", example = "85.0")
  private double percentageUsed;

  @Schema(description = "Start date of the budget period.", example = "2025-10-01")
  private LocalDate startDate;

  @Schema(description = "End date of the budget period.", example = "2025-10-31")
  private LocalDate endDate;
}