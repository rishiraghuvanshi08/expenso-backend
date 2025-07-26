package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.dto.usertransaction.TransactionStatsDTO;
import com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionRequestDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Category;
import com.expenso.Expenso.entities.UserTransaction;
import com.expenso.Expenso.entities.Wallet;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.repository.CategoryRepository;
import com.expenso.Expenso.repository.UserTransactionRepository;
import com.expenso.Expenso.repository.WalletRepository;
import com.expenso.Expenso.service.UserTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTransactionServiceImpl implements UserTransactionService {

  private final UserTransactionRepository userTransactionRepository;
  private final CategoryRepository categoryRepository;
  private final WalletRepository walletRepository;
  private final AppUserRepository appUserRepository;

  @Override
  public UserTransactionResponseDTO createTransaction(Long userId, UserTransactionRequestDTO dto) {
    AppUser user = appUserRepository.findById(userId).orElseThrow();
    Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();
    Wallet wallet = walletRepository.findById(dto.getWalletId()).orElseThrow();

    UserTransaction tx = new UserTransaction(null, user, category, wallet,
                                             dto.getAmount(), dto.getTransactionType(), dto.getNote(), dto.getDate(), false);

    tx = userTransactionRepository.save(tx);

    return mapToResponse(tx);
  }

  @Override
  public UserTransactionResponseDTO updateTransaction(Long userId, Long id, UserTransactionRequestDTO dto) {
    UserTransaction tx = userTransactionRepository.findByIdAndAppUserIdAndIsActiveTrue(id, userId)
                                                  .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    tx.setAmount(dto.getAmount());
    tx.setCategory(categoryRepository.findById(dto.getCategoryId()).orElseThrow());
    tx.setWallet(walletRepository.findById(dto.getWalletId()).orElseThrow());
    tx.setTransactionType(dto.getTransactionType());
    tx.setNote(dto.getNote());
    tx.setDate(dto.getDate());

    return mapToResponse(userTransactionRepository.save(tx));
  }

  @Override
  public void deleteTransaction(Long userId, Long id) {
    UserTransaction tx = userTransactionRepository.findByIdAndAppUserIdAndIsActiveTrue(id, userId)
                                                  .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    tx.setIsDeleted(true);
    userTransactionRepository.save(tx);
  }

  @Override
  public List<UserTransactionResponseDTO> getAllTransactions(Long userId) {
    return userTransactionRepository.findByAppUserIdAndIsActiveTrue(userId)
                                    .stream()
                                    .map(this::mapToResponse)
                                    .collect(Collectors.toList());
  }

  @Override
  public List<TransactionSummaryDTO> getSummary(Long userId, String groupBy) {
    return userTransactionRepository.getSummaryByGroup(userId, groupBy.toUpperCase());
  }

  @Override
  public TransactionStatsDTO getStats(Long userId) {
    List<Object[]> results = userTransactionRepository.getMonthlyStats(userId);

    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpense = BigDecimal.ZERO;
    Map<String, BigDecimal> monthly = new LinkedHashMap<>();

    for (Object[] row : results) {
      String month = Month.of((Integer) row[0]).name();
      BigDecimal income = (BigDecimal) row[1];
      BigDecimal expense = (BigDecimal) row[2];

      totalIncome = totalIncome.add(income);
      totalExpense = totalExpense.add(expense);

      monthly.put(month, income.subtract(expense));
    }

    return TransactionStatsDTO.builder()
                              .totalIncome(totalIncome)
                              .totalExpense(totalExpense)
                              .netBalance(totalIncome.subtract(totalExpense))
                              .monthlyTotals(monthly)
                              .build();
  }

  private UserTransactionResponseDTO mapToResponse(UserTransaction tx) {
    return UserTransactionResponseDTO.builder()
                                     .id(tx.getId())
                                     .categoryName(tx.getCategory().getName())
                                     .walletName(tx.getWallet().getName())
                                     .amount(tx.getAmount())
                                     .transactionType(tx.getTransactionType())
                                     .note(tx.getNote())
                                     .date(tx.getDate())
                                     .build();
  }
}
