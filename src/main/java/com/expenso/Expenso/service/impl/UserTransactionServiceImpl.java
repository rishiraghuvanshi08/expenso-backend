package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.dto.usertransaction.*;
import com.expenso.Expenso.entities.*;
import com.expenso.Expenso.enums.entity.TransactionType;
import com.expenso.Expenso.enums.request.TransactionGroupBy;
import com.expenso.Expenso.enums.response.*;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.repository.*;
import com.expenso.Expenso.service.UserTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
  private final BudgetRepository budgetRepository;

  @Override
  public UserTransactionResponseDTO createTransaction(Long userId, UserTransactionRequestDTO dto) {
    AppUser user = findUserOrThrow(userId);
    Category category = findCategoryOrThrow(dto.getCategoryId());

    UserTransaction tx = new UserTransaction();
    tx.setAppUser(user);
    tx.setCategory(category);
    tx.setAmount(dto.getAmount());
    tx.setTransactionType(dto.getTransactionType());
    tx.setNote(dto.getNote());
    tx.setDate(dto.getDate());

    // If wallet is selected, update balance and associate it
    if (dto.getWalletId() != null) {
      Wallet wallet = findWalletOrThrow(dto.getWalletId());

      if (dto.getTransactionType() == TransactionType.EXPENSE) {
        wallet.setBalance(wallet.getBalance().subtract(dto.getAmount()));
      } else if (dto.getTransactionType() == TransactionType.INCOME) {
        wallet.setBalance(wallet.getBalance().add(dto.getAmount()));
      }

      walletRepository.save(wallet); // Persist wallet changes
      tx.setWallet(wallet);          // Set wallet in transaction
    }

    tx = userTransactionRepository.save(tx);

    return mapToResponse(tx);
  }

  @Override
  public UserTransactionResponseDTO updateTransaction(Long userId, Long transactionId, UserTransactionUpdateDTO dto) {
    UserTransaction tx = userTransactionRepository.findByIdAndAppUserIdAndIsDeletedFalse(transactionId, userId)
                                                  .orElseThrow(() -> new ResourceNotFoundException(UserTransactionResponseMessage.TRANSACTION_NOT_FOUND.getMessage()));

    // Only allow updates to non-financial fields
    tx.setCategory(findCategoryOrThrow(dto.getCategoryId()));
    tx.setNote(dto.getNote());
    tx.setDate(dto.getDate());

    return mapToResponse(userTransactionRepository.save(tx));
  }

  @Override
  public void deleteTransaction(Long userId, Long transactionId) {
    UserTransaction tx = userTransactionRepository.findByIdAndAppUserIdAndIsDeletedFalse(transactionId, userId)
                                                  .orElseThrow(() -> new ResourceNotFoundException(UserTransactionResponseMessage.TRANSACTION_NOT_FOUND.getMessage()));

    // Reverse effect on wallet
    Wallet wallet = tx.getWallet();
    BigDecimal amount = tx.getAmount();

    if (wallet != null) {
      if (tx.getTransactionType() == TransactionType.EXPENSE) {
        wallet.setBalance(wallet.getBalance().add(amount));
      } else if (tx.getTransactionType() == TransactionType.INCOME) {
        wallet.setBalance(wallet.getBalance().subtract(amount));
      }

      walletRepository.save(wallet);
    }

    // Mark transaction as deleted (soft delete)
    tx.setIsDeleted(true);
    userTransactionRepository.save(tx);
  }

  @Override
  public List<UserTransactionResponseDTO> getAllTransactions(Long userId) {
    return userTransactionRepository.findByAppUserIdAndIsDeletedFalse(userId)
                                    .stream()
                                    .map(this::mapToResponse)
                                    .collect(Collectors.toList());
  }

  @Override
  public List<TransactionSummaryDTO> getSummary(Long userId, TransactionGroupBy groupBy) {
    return switch (groupBy) {
      case CATEGORY -> userTransactionRepository.getSummaryByCategory(userId);
      case WALLET -> userTransactionRepository.getSummaryByWallet(userId);
      case DATE -> userTransactionRepository.getSummaryByDate(userId);
      case MONTH -> userTransactionRepository.getSummaryByMonth(userId);
      case YEAR -> userTransactionRepository.getSummaryByYear(userId);
    };
  }

  @Override
  public TransactionStatsDTO getStats(Long userId) {
    List<Object[]> results = userTransactionRepository.getMonthlyStats(userId);

    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpense = BigDecimal.ZERO;
    Map<String, BigDecimal> monthly = new LinkedHashMap<>();

    for (Object[] row : results) {
      int year = (int) row[0];
      int month = (int) row[1];
      BigDecimal income = toBigDecimal(row[2]);
      BigDecimal expense = toBigDecimal(row[3]);

      totalIncome = totalIncome.add(income);
      totalExpense = totalExpense.add(expense);

      // Format label like "June 2024"
      String label = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;

      monthly.put(label, income.subtract(expense));
    }

    return TransactionStatsDTO.builder()
                              .totalIncome(totalIncome)
                              .totalExpense(totalExpense)
                              .netBalance(totalIncome.subtract(totalExpense))
                              .monthlyTotals(monthly)
                              .build();
  }

  @Override
  public List<UserTransactionResponseDTO> getTransactionsByCategory(Long userId, Long categoryId) {
    return userTransactionRepository.findByAppUserIdAndCategoryIdAndIsDeletedFalse(userId, categoryId)
                                    .stream()
                                    .map(this::mapToResponse)
                                    .collect(Collectors.toList());
  }

  @Override
  public List<UserTransactionResponseDTO> getTransactionsByBudget(Long userId, Long budgetId) {
    Budget budget = budgetRepository.findByIdAndAppUserId(budgetId, userId)
                                    .orElseThrow(() -> new ResourceNotFoundException(BudgetResponseMessage.BUDGET_NOT_FOUND.getMessage()));

    return userTransactionRepository
             .findByAppUserIdAndCategoryIdAndDateBetweenAndIsDeletedFalse(
               userId,
               budget.getCategory().getId(),
               budget.getStartDate(),
               budget.getEndDate()
             )
             .stream()
             .map(this::mapToResponse)
             .collect(Collectors.toList());
  }

  private BigDecimal toBigDecimal(Object obj) {
    if (obj instanceof BigDecimal) return (BigDecimal) obj;
    if (obj instanceof Integer) return BigDecimal.valueOf((Integer) obj);
    if (obj instanceof Long) return BigDecimal.valueOf((Long) obj);
    if (obj instanceof Double) return BigDecimal.valueOf((Double) obj);
    if (obj instanceof Float) return BigDecimal.valueOf((Float) obj);
    return BigDecimal.ZERO;
  }

  private AppUser findUserOrThrow(Long id) {
    return appUserRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(AppUserResponseMessage.USER_NOT_FOUND.getMessage()));
  }

  private Category findCategoryOrThrow(Long categoryId){
    return categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new ResourceNotFoundException((CategoryResponseMessage.CATEGORY_NOT_FOUND.getMessage())));
  }

  private Wallet findWalletOrThrow(Long walletId){
    return walletRepository.findById(walletId).orElseThrow(() -> new ResourceNotFoundException(WalletResponseMessage.WALLET_NOT_FOUND.getMessage()));
  }

  private UserTransactionResponseDTO mapToResponse(UserTransaction tx) {
    return UserTransactionResponseDTO.builder()
                                     .id(tx.getId())
                                     .categoryName(tx.getCategory().getName())
                                     .walletName(
                                       tx.getWallet() != null ? tx.getWallet().getName() : null
                                     )
                                     .amount(tx.getAmount())
                                     .transactionType(tx.getTransactionType())
                                     .note(tx.getNote())
                                     .date(tx.getDate())
                                     .build();
  }
}
