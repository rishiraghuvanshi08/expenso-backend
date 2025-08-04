package com.expenso.Expenso.dto.budget;

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
public class BudgetRequestDTO {
  private Long categoryId;
  private BigDecimal amount;
  private LocalDate startDate;
  private LocalDate endDate;
}