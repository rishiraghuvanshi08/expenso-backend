package com.expenso.Expenso.dto.usertransaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatsDTO {
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;
  private BigDecimal netBalance;
  private Map<String, BigDecimal> monthlyTotals;
}