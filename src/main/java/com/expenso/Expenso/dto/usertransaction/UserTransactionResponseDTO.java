package com.expenso.Expenso.dto.usertransaction;

import com.expenso.Expenso.enums.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response object containing detailed transaction information.")
public class UserTransactionResponseDTO {

  @Schema(description = "Unique identifier of the transaction.", example = "101")
  private Long id;

  @Schema(description = "Name of the transaction category.", example = "Food & Dining")
  private String categoryName;

  @Schema(description = "Name of the wallet from which the transaction was made.", example = "Main Wallet")
  private String walletName;

  @Schema(description = "Amount of the transaction.", example = "85.50")
  private BigDecimal amount;

  @Schema(description = "Type of the transaction (INCOME or EXPENSE).", example = "EXPENSE")
  private TransactionType transactionType;

  @Schema(description = "Optional note or comment added to the transaction.", example = "Dinner at Italian restaurant")
  private String note;

  @Schema(description = "Date when the transaction occurred.", example = "2025-10-28")
  private LocalDate date;
}