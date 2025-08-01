package com.expenso.Expenso.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
  private Long id;
  private BigDecimal amount;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean active;
}