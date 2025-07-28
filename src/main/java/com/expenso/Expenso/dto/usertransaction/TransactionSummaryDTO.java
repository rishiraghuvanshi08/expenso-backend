package com.expenso.Expenso.dto.usertransaction;

import com.expenso.Expenso.enums.request.TransactionGroupBy;
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
public class TransactionSummaryDTO {
  private TransactionGroupBy groupBy; // category/date/wallet
  private String label;
  private BigDecimal incomeTotal;
  private BigDecimal expenseTotal;
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