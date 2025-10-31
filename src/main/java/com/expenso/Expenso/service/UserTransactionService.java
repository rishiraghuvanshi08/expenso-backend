package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.usertransaction.*;
import com.expenso.Expenso.enums.request.TransactionGroupBy;

import java.util.List;

/**
 * Service interface for managing personal user transactions in Expenso.
 * Provides operations for creating, updating, deleting, and retrieving
 * transactions, as well as generating summaries and statistics.
 */
public interface UserTransactionService {

  /**
   * Creates a new transaction for the specified user.
   *
   * @param userId ID of the user creating the transaction.
   * @param dto    Transaction details for creation.
   * @return Created transaction details as {@link UserTransactionResponseDTO}.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if user or category is not found.
   */
  UserTransactionResponseDTO createTransaction(Long userId, UserTransactionRequestDTO dto);

  /**
   * Updates an existing transaction with new details.
   * Only allows modification of non-financial fields (e.g., note, date, category).
   *
   * @param userId        ID of the transaction owner.
   * @param transactionId ID of the transaction to update.
   * @param dto           Update details.
   * @return Updated transaction details.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if transaction or category is not found.
   */
  UserTransactionResponseDTO updateTransaction(Long userId, Long transactionId, UserTransactionUpdateDTO dto);

  /**
   * Soft deletes a transaction and reverses its effect on the wallet balance.
   *
   * @param userId        ID of the transaction owner.
   * @param transactionId ID of the transaction to delete.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if transaction is not found.
   */
  void deleteTransaction(Long userId, Long transactionId);

  /**
   * Retrieves all active (non-deleted) transactions for a user.
   *
   * @param userId ID of the user.
   * @return List of {@link UserTransactionResponseDTO}.
   */
  List<UserTransactionResponseDTO> getAllTransactions(Long userId);

  /**
   * Retrieves summarized transaction data grouped by a specified parameter.
   *
   * @param userId  ID of the user.
   * @param groupBy Field by which to group transactions (e.g., CATEGORY, WALLET, MONTH).
   * @return List of summarized results as {@link TransactionSummaryDTO}.
   */
  List<TransactionSummaryDTO> getSummary(Long userId, TransactionGroupBy groupBy);

  /**
   * Retrieves overall income, expense, and net balance statistics for a user.
   *
   * @param userId ID of the user.
   * @return Transaction statistics as {@link TransactionStatsDTO}.
   */
  TransactionStatsDTO getStats(Long userId);

  /**
   * Retrieves all transactions for a given category.
   *
   * @param userId     ID of the user.
   * @param categoryId ID of the category.
   * @return List of {@link UserTransactionResponseDTO}.
   */
  List<UserTransactionResponseDTO> getTransactionsByCategory(Long userId, Long categoryId);

  /**
   * Retrieves transactions associated with a specific budget period.
   *
   * @param userId   ID of the user.
   * @param budgetId ID of the budget.
   * @return List of {@link UserTransactionResponseDTO}.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if budget is not found.
   */
  List<UserTransactionResponseDTO> getTransactionsByBudget(Long userId, Long budgetId);
}
