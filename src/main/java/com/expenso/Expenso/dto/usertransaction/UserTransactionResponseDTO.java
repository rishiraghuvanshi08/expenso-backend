package com.expenso.Expenso.dto.usertransaction;

import com.expenso.Expenso.enums.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionResponseDTO {
  private Long id;
  private String categoryName;
  private String walletName;
  private BigDecimal amount;
  private TransactionType transactionType;
  private String note;
  private LocalDate date;
}