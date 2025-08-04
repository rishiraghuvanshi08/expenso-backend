package com.expenso.Expenso.dto.budget;

import com.expenso.Expenso.enums.entity.BudgetStatus;
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
public class BudgetResponseDTO {
  private Long id;
  private Long categoryId;
  private String categoryName;
  private BigDecimal amount;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean active;
  private BudgetStatus status;
  private BigDecimal totalSpent;
}