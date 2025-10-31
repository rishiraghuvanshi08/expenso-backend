package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.budget.BudgetAnalyticsDTO;
import com.expenso.Expenso.dto.budget.BudgetRequestDTO;
import com.expenso.Expenso.dto.budget.BudgetResponseDTO;

import java.util.List;

/**
 * Service interface defining operations for managing user budgets.
 * Handles CRUD operations and analytics for personal budgets.
 */
public interface BudgetService {

  /**
   * Fetches all budgets for the given user.
   *
   * @param userId Authenticated user ID
   * @return List of user budgets
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException User not found
   */
  List<BudgetResponseDTO> getAllBudgets(Long userId);

  /**
   * Creates a new budget for a given user.
   *
   * @param userId Authenticated user ID
   * @param dto Budget details
   * @return Created budget response
   * @throws IllegalArgumentException Details are not valid
   * @throws IllegalStateException Overlapping budget exists
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException User or Budget not found
   */
  BudgetResponseDTO createBudget(Long userId, BudgetRequestDTO dto);

  /**
   * Updates an existing budget.
   *
   * @param userId Authenticated user ID
   * @param budgetId Budget identifier
   * @param dto Updated budget details
   * @return Updated budget response
   * @throws IllegalArgumentException Details are not valid
   * @throws IllegalStateException Overlapping budget exists
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException User or Budget not found
   */
  BudgetResponseDTO updateBudget(Long userId, Long budgetId, BudgetRequestDTO dto);

  /**
   * Deletes an existing budget by ID.
   *
   * @param userId Authenticated user ID
   * @param budgetId Budget identifier
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException User of Budget not found
   */
  void deleteBudget(Long userId, Long budgetId);

  /**
   * Provides analytics (spent vs. budgeted) for all user budgets.
   *
   * @param userId Authenticated user ID
   * @return List of budget analytics
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException User not found
   */
  List<BudgetAnalyticsDTO> getBudgetAnalytics(Long userId);
}