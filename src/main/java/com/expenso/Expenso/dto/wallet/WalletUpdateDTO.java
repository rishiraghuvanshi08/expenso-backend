package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.enums.entity.WalletType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletUpdateDTO {

  @NotBlank(message = "Wallet name is required.")
  private String name;

  @NotNull(message = "Wallet type is required.")
  private WalletType walletType;
}