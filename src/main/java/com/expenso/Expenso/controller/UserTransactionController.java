package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.usertransaction.*;
import com.expenso.Expenso.enums.request.TransactionGroupBy;
import com.expenso.Expenso.enums.response.UserTransactionResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.UserTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user transactions.
 *
 * Base URL: /api/v1/user-transactions
 *
 * Provides endpoints for creating, updating, deleting, and fetching transactions
 * for the authenticated user. Includes summary and statistical data.
 */
@RestController
@RequestMapping("/api/v1/user-transactions")
@RequiredArgsConstructor
@Tag(name = "User Transactions APIs", description = "APIs for managing user's personal transactions, summaries and statistics")
public class UserTransactionController {

  private final UserTransactionService transactionService;

  /**
   * Fetches all active transactions for the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter)
   */
  @Operation(
    summary = "Get all transactions",
    description = "Retrieves all non-deleted transactions for the authenticated user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Transactions fetched successfully")
  })
  @GetMapping
  public ResponseEntity<CustomResponse<List<UserTransactionResponseDTO>>> getAllTransactions(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<List<UserTransactionResponseDTO>>(true, UserTransactionResponseMessage.TRANSACTION_FETCH_SUCCESS.getMessage(), transactionService.getAllTransactions(userId)));
  }

  /**
   * Creates a new transaction for the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter)
   */
  @Operation(
    summary = "Create transaction",
    description = "Creates a new transaction for the authenticated user and updates wallet balance if applicable."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
    @ApiResponse(responseCode = "404", description = "User or Category not found")
  })
  @PostMapping
  public ResponseEntity<CustomResponse<UserTransactionResponseDTO>> createUserTransaction(@RequestAttribute("userId") Long userId, @RequestBody @Valid UserTransactionRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<UserTransactionResponseDTO>(true, UserTransactionResponseMessage.TRANSACTION_CREATED_SUCCESS.getMessage(), transactionService.createTransaction(userId, dto)));
  }

  /**
   * Updates an existing transaction for the authenticated user.
   * Only non-financial fields like category, note, and date are updatable.
   *
   * @param userId        ID of the authenticated user (injected via JWT filter)
   * @param transactionId ID of the transaction to update
   * @param dto           Request payload with updated details
   * @return Updated transaction details
   */
  @Operation(
    summary = "Update transaction",
    description = "Updates an existing user transaction. Only category, note, and date can be modified."
  )
  @Parameter(name = "transactionId", description = "Unique identifier of the transaction.", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
    @ApiResponse(responseCode = "404", description = "Transaction or Category not found")
  })
  @PutMapping("/{transactionId}")
  public ResponseEntity<CustomResponse<UserTransactionResponseDTO>> updateUserTransaction(@RequestAttribute("userId") Long userId, @PathVariable("transactionId") Long transactionId, @RequestBody @Valid UserTransactionUpdateDTO dto) {
    return ResponseEntity.ok(new CustomResponse<UserTransactionResponseDTO>(true, UserTransactionResponseMessage.TRANSACTION_UPDATED_SUCCESS.getMessage(), transactionService.updateTransaction(userId, transactionId, dto)));
  }

  /**
   * Soft deletes a transaction for the authenticated user.
   * Reverses the wallet balance effect before marking as deleted.
   *
   * @param userId        ID of the authenticated user (injected via JWT filter)
   * @param transactionId ID of the transaction to delete
   */
  @Operation(
    summary = "Delete transaction",
    description = "Soft deletes a transaction and reverses wallet balance impact if applicable."
  )
  @Parameter(name = "transactionId", description = "Unique identifier of the transaction.", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Transaction deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Transaction not found")
  })
  @DeleteMapping("/{transactionId}")
  public ResponseEntity<CustomResponseMessage> deleteUserTransaction(@RequestAttribute("userId") Long userId, @PathVariable("transactionId") Long transactionId) {
    transactionService.deleteTransaction(userId, transactionId);
    return ResponseEntity.ok(new CustomResponseMessage(true, UserTransactionResponseMessage.TRANSACTION_DELETED_SUCCESS.getMessage()));
  }

  /**
   * Retrieves a summary of transactions grouped by a specified parameter.
   *
   * @param userId  ID of the authenticated user (injected via JWT filter)
   * @param groupBy The field to group transactions by (e.g. CATEGORY, WALLET, DATE)
   * @return List of summarized transaction data
   */
  @Operation(
    summary = "Get transaction summary",
    description = "Returns summarized transaction data grouped by category, wallet, date, month, or year."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Transaction summary fetched successfully")
  })
  @GetMapping("/summary")
  public ResponseEntity<CustomResponse<List<TransactionSummaryDTO>>> summary(@RequestAttribute("userId") Long userId, @RequestParam TransactionGroupBy groupBy) {
    return ResponseEntity.ok(new CustomResponse<List<TransactionSummaryDTO>>(true, UserTransactionResponseMessage.TRANSACTION_SUMMARY_FETCH_SUCCESS.getMessage(), transactionService.getSummary(userId, groupBy)));
  }

  /**
   * Fetches overall income, expense, and balance statistics for the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter)
   * @return Transaction statistics
   */
  @Operation(
    summary = "Get transaction stats",
    description = "Provides monthly income, expense, and balance statistics for the authenticated user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Transaction stats fetched successfully")
  })
  @GetMapping("/stats")
  public ResponseEntity<CustomResponse<TransactionStatsDTO>> stats(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<TransactionStatsDTO>(true, UserTransactionResponseMessage.TRANSACTION_STATS_FETCH_SUCCESS.getMessage(), transactionService.getStats(userId)));
  }

  /**
   * Fetches transactions belonging to a specific category.
   *
   * @param userId     ID of the authenticated user (injected via JWT filter)
   * @param categoryId ID of the category
   * @return List of transactions under the given category
   */
  @Operation(
    summary = "Get transactions by category",
    description = "Retrieves all transactions of the authenticated user that belong to a specific category."
  )
  @Parameter(name = "categoryId", description = "Unique identifier of the category.", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Transactions fetched successfully")
  })
  @GetMapping("/category/{categoryId}")
  public ResponseEntity<CustomResponse<List<UserTransactionResponseDTO>>> getTransactionsByCategory(@RequestAttribute("userId") Long userId, @PathVariable Long categoryId) {
    return ResponseEntity.ok(new CustomResponse<>(true, UserTransactionResponseMessage.TRANSACTION_FETCH_SUCCESS.getMessage(), transactionService.getTransactionsByCategory(userId, categoryId)));
  }

  /**
   * Fetches transactions linked to a specific budget period.
   *
   * @param userId   ID of the authenticated user (injected via JWT filter)
   * @param budgetId ID of the budget
   * @return List of transactions related to the given budget
   */
  @Operation(
    summary = "Get transactions by budget",
    description = "Retrieves all transactions that fall within the specified budget's date range and category."
  )
  @Parameter(name = "budgetId", description = "Unique identifier of the budget.", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Transactions fetched successfully"),
    @ApiResponse(responseCode = "404", description = "Budget not found")
  })
  @GetMapping("/budget/{budgetId}")
  public ResponseEntity<CustomResponse<List<UserTransactionResponseDTO>>> getTransactionsByBudget(@RequestAttribute("userId") Long userId, @PathVariable Long budgetId) {
    return ResponseEntity.ok(new CustomResponse<>(true, UserTransactionResponseMessage.TRANSACTION_FETCH_SUCCESS.getMessage(), transactionService.getTransactionsByBudget(userId, budgetId)));
  }

}