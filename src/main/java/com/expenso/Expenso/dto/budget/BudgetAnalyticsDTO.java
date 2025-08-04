package com.expenso.Expenso.dto.budget;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAnalyticsDTO {
  private String categoryName;
  private BigDecimal budgetedAmount;
  private BigDecimal actualSpent;
  private BigDecimal remaining;
  private double percentageUsed;
  private LocalDate startDate;
  private LocalDate endDate;
}