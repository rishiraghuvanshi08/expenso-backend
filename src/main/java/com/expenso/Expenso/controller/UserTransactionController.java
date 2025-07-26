package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.usertransaction.TransactionStatsDTO;
import com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionRequestDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
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
@RequestMapping("/api/v1/user-transactions/{userId}")
@RequiredArgsConstructor
public class UserTransactionController {

  private final UserTransactionService transactionService;

  @GetMapping("/")
  public ResponseEntity<CustomResponse<List<UserTransactionResponseDTO>>> getAllTransactions(@PathVariable("userId") Long userId) {
    //return ResponseEntity.ok(transactionService.getAllTransactions(userId));
    return ResponseEntity.ok(new CustomResponse<List<UserTransactionResponseDTO>>(true, UserTransactionResponseMessage.TRANSACTION_FETCH_SUCCESS.getMessage(), transactionService.getAllTransactions(userId)));
  }

  @PostMapping("/")
  public ResponseEntity<CustomResponse<UserTransactionResponseDTO>> createUserTransaction(@PathVariable("userId") Long userId, @RequestBody @Valid UserTransactionRequestDTO dto) {
    //return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(userId, dto));
    return ResponseEntity.ok(new CustomResponse<UserTransactionResponseDTO>(true, UserTransactionResponseMessage.TRANSACTION_CREATED_SUCCESS.getMessage(), transactionService.createTransaction(userId, dto)));
  }

  @PutMapping("/transaction/{transactionId}")
  public ResponseEntity<CustomResponse<UserTransactionResponseDTO>> updateUserTransaction(@PathVariable("userId") Long userId, @PathVariable("transactionId") Long transactionId, @RequestBody @Valid UserTransactionRequestDTO dto) {
    //return ResponseEntity.ok(transactionService.updateTransaction(userId, transactionId, dto));
    return ResponseEntity.ok(new CustomResponse<UserTransactionResponseDTO>(true, UserTransactionResponseMessage.TRANSACTION_UPDATED_SUCCESS.getMessage(), transactionService.updateTransaction(userId, transactionId, dto)));
  }

  @DeleteMapping("/transaction/{transactionId}")
  public ResponseEntity<?> delete(@PathVariable("userId") Long userId, @PathVariable("transactionId") Long transactionId) {
    transactionService.deleteTransaction(userId, transactionId);
    return ResponseEntity.ok(new CustomResponseMessage(true, UserTransactionResponseMessage.TRANSACTION_DELETED_SUCCESS.getMessage()));
  }

  @GetMapping("/summary")
  public ResponseEntity<CustomResponse<List<TransactionSummaryDTO>>> summary(@PathVariable("userId") Long userId, @RequestParam String groupBy) {
    //return ResponseEntity.ok(transactionService.getSummary(userId, groupBy));
    return ResponseEntity.ok(new CustomResponse<List<TransactionSummaryDTO>>(true, UserTransactionResponseMessage.TRANSACTION_SUMMARY_FETCH_SUCCESS.getMessage(), transactionService.getSummary(userId, groupBy)));
  }

  @GetMapping("/stats")
  public ResponseEntity<CustomResponse<TransactionStatsDTO>> stats(@PathVariable("userId") Long userId) {
    //return ResponseEntity.ok(transactionService.getStats(userId));
    return ResponseEntity.ok(new CustomResponse<TransactionStatsDTO>(true, UserTransactionResponseMessage.TRANSACTION_STATS_FETCH_SUCCESS.getMessage(), transactionService.getStats(userId)));
  }
}