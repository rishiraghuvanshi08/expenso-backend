package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.wallet.WalletDetailsResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletRequestDTO;
import com.expenso.Expenso.dto.wallet.WalletResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletUpdateDTO;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

  List<WalletResponseDTO> getAllWalletsForUser(Long userId);

  WalletResponseDTO createWallet(Long userId, WalletRequestDTO dto);

  WalletResponseDTO updateWallet(Long userId, Long walletId, WalletUpdateDTO dto);

  void deleteWallet(Long userId, Long walletId);

  BigDecimal getTotalBalance(Long userId);

  WalletDetailsResponseDTO getWalletDetailsWithTransactions(Long userId, Long walletId);
}