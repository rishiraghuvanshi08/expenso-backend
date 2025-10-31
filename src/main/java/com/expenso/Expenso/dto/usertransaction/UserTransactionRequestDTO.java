package com.expenso.Expenso.dto.usertransaction;

import com.expenso.Expenso.enums.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Request object for creating a new user transaction.")
public class UserTransactionRequestDTO {

  @NotNull
  @Schema(description = "Amount of the transaction.", example = "2500.00")
  private BigDecimal amount;

  @NotNull
  @Schema(description = "ID of the category associated with this transaction.", example = "3")
  private Long categoryId;

  @Schema(description = "ID of the wallet linked to this transaction.", example = "1")
  private Long walletId;

  @NotNull
  @Schema(description = "Type of the transaction (INCOME or EXPENSE).", example = "EXPENSE")
  private TransactionType transactionType;

  @Schema(description = "Optional note describing the transaction.", example = "Grocery shopping at Walmart")
  private String note;

  @NotNull
  @Schema(description = "Date when the transaction occurred.", example = "2025-10-30")
  private LocalDate date;
}
