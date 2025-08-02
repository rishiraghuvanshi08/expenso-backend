package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
import com.expenso.Expenso.enums.entity.WalletType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class WalletDetailsResponseDTO {

  private Long walletId;
  private String name;
  private WalletType walletType;
  private BigDecimal balance;

  private List<UserTransactionResponseDTO> transactions;
}