package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletDetailsResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletRequestDTO;
import com.expenso.Expenso.dto.wallet.WalletResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletUpdateDTO;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.UserTransaction;
import com.expenso.Expenso.entities.Wallet;
import com.expenso.Expenso.enums.entity.WalletStatus;
import com.expenso.Expenso.enums.entity.WalletType;
import com.expenso.Expenso.enums.response.AppUserResponseMessage;
import com.expenso.Expenso.enums.response.WalletResponseMessage;
import com.expenso.Expenso.exception.custom.InvalidRequestException;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.repository.UserTransactionRepository;
import com.expenso.Expenso.repository.WalletRepository;
import com.expenso.Expenso.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final AppUserRepository appUserRepository;
  private final UserTransactionRepository userTransactionRepository;

  @Override
  public List<WalletResponseDTO> getAllWalletsForUser(Long userId) {
    return walletRepository.findAllByAppUserIdAndStatus(userId, WalletStatus.ACTIVE).stream()
                           .map(this::mapToDTO)
                           .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public WalletResponseDTO createWallet(Long userId, WalletRequestDTO dto) {
    AppUser user = appUserRepository.findById(userId)
                                    .orElseThrow(() -> new ResourceNotFoundException(AppUserResponseMessage.USER_NOT_FOUND.getMessage()));

    validateUniqueWalletNameAndType(userId, dto.getName(), dto.getWalletType(), null);

    Wallet wallet = new Wallet();
    wallet.setAppUser(user);
    wallet.setName(dto.getName());
    wallet.setWalletType(dto.getWalletType());
    wallet.setBalance(dto.getBalance());
    wallet.setStatus(WalletStatus.ACTIVE);

    return mapToDTO(walletRepository.save(wallet));
  }

  @Override
  @Transactional
  public WalletResponseDTO updateWallet(Long userId, Long walletId, WalletUpdateDTO dto) {
    Wallet wallet = getValidatedWallet(walletId, userId);
    validateUniqueWalletNameAndType(userId, dto.getName(), dto.getWalletType(), walletId);

    wallet.setName(dto.getName());
    wallet.setWalletType(dto.getWalletType());

    return mapToDTO(walletRepository.save(wallet));
  }

  @Override
  @Transactional
  public void deleteWallet(Long userId, Long walletId) {
    Wallet wallet = getValidatedWallet(walletId, userId);

    if (wallet.getTransactions() != null && !wallet.getTransactions().isEmpty()) {
      throw new InvalidRequestException(WalletResponseMessage.WALLET_DELETION_RESTRICTED.getMessage());
    }

    wallet.setStatus(WalletStatus.DELETED);
    walletRepository.save(wallet);
  }

  @Override
  public BigDecimal getTotalBalance(Long userId) {
    return walletRepository.findTotalBalanceByUserId(userId);
  }

  @Override
  public WalletDetailsResponseDTO getWalletDetailsWithTransactions(Long userId, Long walletId) {
    Wallet wallet = getValidatedWallet(walletId, userId);

    List<UserTransaction> transactions = userTransactionRepository
                                           .findActiveTransactionsByWalletAndUser(walletId, userId);

    List<UserTransactionResponseDTO> transactionDTOs = mapToListUserTransactionResponseDTO(transactions);

    return mapToWalletResponseDTO(wallet, transactionDTOs);
  }

  private Wallet getValidatedWallet(Long walletId, Long userId){
    return walletRepository.findActiveWalletForActiveUser(walletId, userId)
                           .orElseThrow(() -> new ResourceNotFoundException(WalletResponseMessage.WALLET_NOT_FOUND.getMessage()));
  }

  private void validateUniqueWalletNameAndType(Long userId, String name, WalletType type, Long excludeWalletId) {
    boolean exists = walletRepository.existsActiveWalletByNameAndTypeExcludingId(userId, name, type, excludeWalletId);
    if (exists) {
      throw new InvalidRequestException(WalletResponseMessage.WALLET_ALREADY_EXISTS.getMessage());
    }
  }

  private WalletDetailsResponseDTO mapToWalletResponseDTO(Wallet wallet, List<UserTransactionResponseDTO> transactionDTOs){
    return WalletDetailsResponseDTO.builder()
                                   .walletId(wallet.getId())
                                   .name(wallet.getName())
                                   .walletType(wallet.getWalletType())
                                   .balance(wallet.getBalance())
                                   .transactions(transactionDTOs)
                                   .build();
  }

  private List<UserTransactionResponseDTO> mapToListUserTransactionResponseDTO(List<UserTransaction> transactions){
    return transactions.stream()
                       .map(tx -> UserTransactionResponseDTO.builder()
                                                            .id(tx.getId())
                                                            .categoryName(tx.getCategory() != null ? tx.getCategory().getName() : "Uncategorized")
                                                            .walletName(tx.getWallet().getName())
                                                            .amount(tx.getAmount())
                                                            .transactionType(tx.getTransactionType())
                                                            .note(tx.getNote())
                                                            .date(tx.getDate())
                                                            .build())
                       .collect(Collectors.toList());
  }

  private WalletResponseDTO mapToDTO(Wallet wallet) {
    return WalletResponseDTO.builder()
                            .walletId(wallet.getId())
                            .name(wallet.getName())
                            .walletType(wallet.getWalletType())
                            .balance(wallet.getBalance())
                            .build();
  }
}