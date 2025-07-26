package com.expenso.Expenso.dto.usertransaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryDTO {
  private String groupBy; // category/date/wallet
  private String label;
  private BigDecimal totalAmount;
}