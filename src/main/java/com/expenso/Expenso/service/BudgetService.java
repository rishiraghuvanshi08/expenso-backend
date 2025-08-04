package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.budget.BudgetAnalyticsDTO;
import com.expenso.Expenso.dto.budget.BudgetRequestDTO;
import com.expenso.Expenso.dto.budget.BudgetResponseDTO;

import java.util.List;

public interface BudgetService {
  List<BudgetResponseDTO> getAllBudgets(Long userId);
  BudgetResponseDTO createBudget(Long userId, BudgetRequestDTO dto);
  BudgetResponseDTO updateBudget(Long userId, Long budgetId, BudgetRequestDTO dto);
  void deleteBudget(Long userId, Long budgetId);
  List<BudgetAnalyticsDTO> getBudgetAnalytics(Long userId);
}