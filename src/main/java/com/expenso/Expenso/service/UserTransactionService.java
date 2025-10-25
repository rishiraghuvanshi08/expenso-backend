package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.usertransaction.*;
import com.expenso.Expenso.enums.request.TransactionGroupBy;

import java.util.List;

public interface UserTransactionService {

  UserTransactionResponseDTO createTransaction(Long userId, UserTransactionRequestDTO dto);

  UserTransactionResponseDTO updateTransaction(Long userId, Long transactionId, UserTransactionUpdateDTO dto);

  void deleteTransaction(Long userId, Long transactionId);

  List<UserTransactionResponseDTO> getAllTransactions(Long userId);

  List<TransactionSummaryDTO> getSummary(Long userId, TransactionGroupBy groupBy);

  TransactionStatsDTO getStats(Long userId);

  List<UserTransactionResponseDTO> getTransactionsByCategory(Long userId, Long categoryId);

  List<UserTransactionResponseDTO> getTransactionsByBudget(Long userId, Long budgetId);
}
