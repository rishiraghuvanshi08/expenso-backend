package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
import com.expenso.Expenso.enums.entity.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Schema(description = "Detailed wallet information including associated transactions.")
public class WalletDetailsResponseDTO {

  @Schema(description = "Unique identifier of the wallet.", example = "1")
  private Long walletId;

  @Schema(description = "Name of the wallet.", example = "Personal Wallet")
  private String name;

  @Schema(description = "Type of the wallet.", example = "CASH")
  private WalletType walletType;

  @Schema(description = "Current wallet balance.", example = "1250.75")
  private BigDecimal balance;

  @Schema(description = "List of transactions associated with this wallet.")
  private List<UserTransactionResponseDTO> transactions;
}