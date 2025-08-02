package com.expenso.Expenso.dto.wallet;

import com.expenso.Expenso.enums.entity.WalletType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletResponseDTO {
  private Long walletId;
  private String name;
  private WalletType walletType;
  private BigDecimal balance;
}