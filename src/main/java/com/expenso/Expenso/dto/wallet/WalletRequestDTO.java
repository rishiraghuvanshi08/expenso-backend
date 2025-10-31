package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.enums.entity.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request body for creating a new wallet.")
public class WalletRequestDTO {

  @Schema(description = "Name of the wallet.", example = "Savings Wallet")
  @NotBlank(message = "Wallet name is required.")
  private String name;

  @Schema(description = "Type of wallet (e.g., CASH, BANK, CARD).", example = "BANK")
  @NotNull(message = "Wallet type is required.")
  private WalletType walletType;

  @Schema(description = "Initial wallet balance. Must be non-negative.", example = "5000.00")
  @NotNull(message = "Initial balance is required.")
  @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non-negative.")
  private BigDecimal balance;
}