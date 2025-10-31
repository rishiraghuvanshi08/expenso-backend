package com.expenso.Expenso.dto.usertransaction;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Statistical overview of all user transactions including monthly trends.")
public class TransactionStatsDTO {

  @Schema(description = "Total income across all transactions.", example = "15000.00")
  private BigDecimal totalIncome;

  @Schema(description = "Total expenses across all transactions.", example = "12000.00")
  private BigDecimal totalExpense;

  @Schema(description = "Net balance calculated as total income minus total expenses.", example = "3000.00")
  private BigDecimal netBalance;

  @Schema(description = "Map of monthly totals where the key is the month label and the value is the total amount.", example = "{\"January 2025\": 1200.00, \"February 2025\": 1500.00}")
  private Map<String, BigDecimal> monthlyTotals;
}