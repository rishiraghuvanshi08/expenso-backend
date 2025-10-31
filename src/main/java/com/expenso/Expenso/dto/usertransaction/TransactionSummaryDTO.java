package com.expenso.Expenso.dto.usertransaction;

import com.expenso.Expenso.enums.request.TransactionGroupBy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Aggregated summary of transactions grouped by category, date, or wallet.")
public class TransactionSummaryDTO {

  @Schema(description = "Grouping type for the summary (e.g., CATEGORY, DATE, WALLET).", example = "CATEGORY")
  private TransactionGroupBy groupBy; // category/date/wallet

  @Schema(description = "Label representing the grouping value (e.g., 'Food', 'October 2025').", example = "Food & Dining")
  private String label;

  @Schema(description = "Total income for the group.", example = "5000.00")
  private BigDecimal incomeTotal;

  @Schema(description = "Total expense for the group.", example = "3200.00")
  private BigDecimal expenseTotal;

  @Schema(description = "Net balance calculated as income minus expense.", example = "1800.00")
  private BigDecimal balance;

  // Constructor for YEAR or DATE
  public TransactionSummaryDTO(TransactionGroupBy groupBy, Object label, BigDecimal income, BigDecimal expense) {
    this.groupBy = groupBy;
    this.label = String.valueOf(label);
    this.incomeTotal = income;
    this.expenseTotal = expense;
    this.balance = income.subtract(expense);
  }

  // Constructor for MONTH (year + month)
  public TransactionSummaryDTO(TransactionGroupBy groupBy, Object year, Object month, BigDecimal income, BigDecimal expense) {
    this.groupBy = groupBy;
    this.label = formatMonthYear(month, year);
    this.incomeTotal = income;
    this.expenseTotal = expense;
    this.balance = income.subtract(expense);
  }

  private String formatMonthYear(Object monthObj, Object yearObj) {
    int month = Integer.parseInt(monthObj.toString());
    int year = Integer.parseInt(yearObj.toString());
    String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    return monthName + " " + year;
  }
}