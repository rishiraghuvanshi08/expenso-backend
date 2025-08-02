package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.enums.entity.WalletType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRequestDTO {

  @NotBlank(message = "Wallet name is required.")
  private String name;

  @NotNull(message = "Wallet type is required.")
  private WalletType walletType;

  @NotNull(message = "Initial balance is required.")
  @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non-negative.")
  private BigDecimal balance;
}