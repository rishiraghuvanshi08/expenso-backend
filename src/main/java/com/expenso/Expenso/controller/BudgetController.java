package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.budget.BudgetAnalyticsDTO;
import com.expenso.Expenso.dto.budget.BudgetRequestDTO;
import com.expenso.Expenso.dto.budget.BudgetResponseDTO;
import com.expenso.Expenso.enums.response.BudgetResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

  private final BudgetService budgetService;

  @GetMapping
  public ResponseEntity<CustomResponse<List<BudgetResponseDTO>>> getAll(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_LIST_FETCH_SUCCESS.getMessage(), budgetService.getAllBudgets(userId)));
  }

  @PostMapping
  public ResponseEntity<CustomResponse<BudgetResponseDTO>> create(@RequestAttribute("userId") Long userId, @RequestBody BudgetRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_CREATED_SUCCESS.getMessage(), budgetService.createBudget(userId, dto)));
  }

  @PutMapping("/{budgetId}")
  public ResponseEntity<CustomResponse<BudgetResponseDTO>> update(@RequestAttribute("userId") Long userId, @PathVariable("budgetId") Long budgetId, @RequestBody BudgetRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_UPDATED_SUCCESS.getMessage(), budgetService.updateBudget(userId, budgetId, dto)));
  }

  @DeleteMapping("/{budgetId}")
  public ResponseEntity<CustomResponseMessage> delete(@RequestAttribute("userId") Long userId, @PathVariable("budgetId") Long budgetId) {
    budgetService.deleteBudget(userId, budgetId);
    return ResponseEntity.ok(new CustomResponseMessage(true, BudgetResponseMessage.BUDGET_DELETED_SUCCESS.getMessage()));
  }

  @GetMapping("/analysis")
  public ResponseEntity<CustomResponse<List<BudgetAnalyticsDTO>>> getBudgetAnalytics(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_ANALYSIS_FETCH_SUCCESS.getMessage(), budgetService.getBudgetAnalytics(userId)));
  }
}