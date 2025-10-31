package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.enums.entity.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for updating wallet information (excluding balance).")
public class WalletUpdateDTO {

  @Schema(description = "Updated wallet name.", example = "Updated Savings Wallet")
  @NotBlank(message = "Wallet name is required.")
  private String name;

  @Schema(description = "Updated wallet type.", example = "BANK")
  @NotNull(message = "Wallet type is required.")
  private WalletType walletType;
}