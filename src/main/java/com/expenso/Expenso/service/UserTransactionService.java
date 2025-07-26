package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.usertransaction.TransactionStatsDTO;
import com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionRequestDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;

import java.util.List;

public interface UserTransactionService {

  UserTransactionResponseDTO createTransaction(Long userId, UserTransactionRequestDTO dto);

  UserTransactionResponseDTO updateTransaction(Long userId, Long id, UserTransactionRequestDTO dto);

  void deleteTransaction(Long userId, Long id);

  List<UserTransactionResponseDTO> getAllTransactions(Long userId);

  List<TransactionSummaryDTO> getSummary(Long userId, String groupBy);

  TransactionStatsDTO getStats(Long userId);
}
