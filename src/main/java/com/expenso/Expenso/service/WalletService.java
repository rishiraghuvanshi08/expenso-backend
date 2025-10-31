package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.wallet.WalletDetailsResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletRequestDTO;
import com.expenso.Expenso.dto.wallet.WalletResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletUpdateDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing user wallets.
 * Provides operations for creating, updating, deleting,
 * and retrieving wallet information, including associated
 * transactions and total balances.
 */
public interface WalletService {

  /**
   * Fetches all active wallets belonging to a specific user.
   *
   * @param userId ID of the user whose wallets are to be fetched.
   * @return List of {@link WalletResponseDTO} containing wallet summaries.
   */
  List<WalletResponseDTO> getAllWalletsForUser(Long userId);

  /**
   * Creates a new wallet for the given user.
   *
   * @param userId ID of the user creating the wallet.
   * @param dto Wallet creation request payload.
   * @return {@link WalletResponseDTO} representing the newly created wallet.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if the user does not exist.
   * @throws com.expenso.Expenso.exception.custom.InvalidRequestException if a wallet with the same name and type already exists.
   */
  WalletResponseDTO createWallet(Long userId, WalletRequestDTO dto);

  /**
   * Updates an existing wallet for the given user.
   *
   * @param userId ID of the wallet owner.
   * @param walletId ID of the wallet to update.
   * @param dto Wallet update request payload.
   * @return Updated {@link WalletResponseDTO}.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if the wallet or user does not exist.
   * @throws com.expenso.Expenso.exception.custom.InvalidRequestException if a duplicate wallet name and type combination is detected.
   */
  WalletResponseDTO updateWallet(Long userId, Long walletId, WalletUpdateDTO dto);

  /**
   * Deletes (soft deletes) a wallet if it has no active transactions.
   *
   * @param userId ID of the wallet owner.
   * @param walletId ID of the wallet to delete.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if the wallet or user does not exist.
   * @throws com.expenso.Expenso.exception.custom.InvalidRequestException if wallet contains transactions.
   */
  void deleteWallet(Long userId, Long walletId);

  /**
   * Retrieves the total balance across all active wallets of a user.
   *
   * @param userId ID of the user.
   * @return Total balance as {@link BigDecimal}.
   */
  BigDecimal getTotalBalance(Long userId);

  /**
   * Fetches a wallet’s full details, including linked transactions.
   *
   * @param userId ID of the wallet owner.
   * @param walletId ID of the wallet.
   * @return {@link WalletDetailsResponseDTO} with wallet details and transactions.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if the wallet or user does not exist.
   */
  WalletDetailsResponseDTO getWalletDetailsWithTransactions(Long userId, Long walletId);
}