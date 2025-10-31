package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.enums.entity.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Represents wallet summary information.")
public class WalletResponseDTO {

  @Schema(description = "Unique identifier of the wallet.", example = "1")
  private Long walletId;

  @Schema(description = "Name of the wallet.", example = "Travel Wallet")
  private String name;

  @Schema(description = "Type of wallet (e.g., CASH, BANK, CARD).", example = "CASH")
  private WalletType walletType;

  @Schema(description = "Current wallet balance.", example = "3400.50")
  private BigDecimal balance;
}