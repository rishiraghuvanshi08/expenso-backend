package com.expenso.Expenso.dto.usertransaction;

import com.expenso.Expenso.enums.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserTransactionRequestDTO {

  @NotNull
  private BigDecimal amount;

  @NotNull
  private Long categoryId;

  @NotNull
  private Long walletId;

  @NotNull
  private TransactionType transactionType;

  private String note;

  @NotNull
  private LocalDate date;
}
