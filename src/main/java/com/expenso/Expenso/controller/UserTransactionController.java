package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.usertransaction.*;
import com.expenso.Expenso.enums.request.TransactionGroupBy;
import com.expenso.Expenso.enums.response.UserTransactionResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.UserTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-transactions")
@RequiredArgsConstructor
public class UserTransactionController {

  private final UserTransactionService transactionService;

  @GetMapping
  public ResponseEntity<CustomResponse<List<UserTransactionResponseDTO>>> getAllTransactions(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<List<UserTransactionResponseDTO>>(true, UserTransactionResponseMessage.TRANSACTION_FETCH_SUCCESS.getMessage(), transactionService.getAllTransactions(userId)));
  }

  @PostMapping
  public ResponseEntity<CustomResponse<UserTransactionResponseDTO>> createUserTransaction(@RequestAttribute("userId") Long userId, @RequestBody @Valid UserTransactionRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<UserTransactionResponseDTO>(true, UserTransactionResponseMessage.TRANSACTION_CREATED_SUCCESS.getMessage(), transactionService.createTransaction(userId, dto)));
  }

  @PutMapping("/{transactionId}")
  public ResponseEntity<CustomResponse<UserTransactionResponseDTO>> updateUserTransaction(@RequestAttribute("userId") Long userId, @PathVariable("transactionId") Long transactionId, @RequestBody @Valid UserTransactionUpdateDTO dto) {
    return ResponseEntity.ok(new CustomResponse<UserTransactionResponseDTO>(true, UserTransactionResponseMessage.TRANSACTION_UPDATED_SUCCESS.getMessage(), transactionService.updateTransaction(userId, transactionId, dto)));
  }

  @DeleteMapping("/{transactionId}")
  public ResponseEntity<CustomResponseMessage> deleteUserTransaction(@RequestAttribute("userId") Long userId, @PathVariable("transactionId") Long transactionId) {
    transactionService.deleteTransaction(userId, transactionId);
    return ResponseEntity.ok(new CustomResponseMessage(true, UserTransactionResponseMessage.TRANSACTION_DELETED_SUCCESS.getMessage()));
  }

  @GetMapping("/summary")
  public ResponseEntity<CustomResponse<List<TransactionSummaryDTO>>> summary(@RequestAttribute("userId") Long userId, @RequestParam TransactionGroupBy groupBy) {
    return ResponseEntity.ok(new CustomResponse<List<TransactionSummaryDTO>>(true, UserTransactionResponseMessage.TRANSACTION_SUMMARY_FETCH_SUCCESS.getMessage(), transactionService.getSummary(userId, groupBy)));
  }

  @GetMapping("/stats")
  public ResponseEntity<CustomResponse<TransactionStatsDTO>> stats(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<TransactionStatsDTO>(true, UserTransactionResponseMessage.TRANSACTION_STATS_FETCH_SUCCESS.getMessage(), transactionService.getStats(userId)));
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<CustomResponse<List<UserTransactionResponseDTO>>> getTransactionsByCategory(@RequestAttribute("userId") Long userId, @PathVariable Long categoryId) {
    return ResponseEntity.ok(new CustomResponse<>(true, UserTransactionResponseMessage.TRANSACTION_FETCH_SUCCESS.getMessage(), transactionService.getTransactionsByCategory(userId, categoryId)));
  }

  @GetMapping("/budget/{budgetId}")
  public ResponseEntity<CustomResponse<List<UserTransactionResponseDTO>>> getTransactionsByBudget(@RequestAttribute("userId") Long userId, @PathVariable Long budgetId) {
    return ResponseEntity.ok(new CustomResponse<>(true, UserTransactionResponseMessage.TRANSACTION_FETCH_SUCCESS.getMessage(), transactionService.getTransactionsByBudget(userId, budgetId)));
  }

}