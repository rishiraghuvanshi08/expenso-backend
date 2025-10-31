package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.budget.BudgetAnalyticsDTO;
import com.expenso.Expenso.dto.budget.BudgetRequestDTO;
import com.expenso.Expenso.dto.budget.BudgetResponseDTO;
import com.expenso.Expenso.enums.response.BudgetResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user budgets, including creation, updates,
 * deletion, retrieval, and analytics for personal budgeting.
 *
 * Base URL: /api/v1/budgets
 */
@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Budget Management APIs", description = "Provides APIs to manage personal budgets, track spending, and analyze budget usage over time.")
public class BudgetController {

  private final BudgetService budgetService;

  /**
   * Retrieves all budgets belonging to the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @return List of budgets associated with the user.
   */
  @Operation(
    summary = "Get all budgets for user",
    description = "Fetches all personal budgets created by the logged-in user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget list fetched successfully"),
    @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping
  public ResponseEntity<CustomResponse<List<BudgetResponseDTO>>> getAll(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_LIST_FETCH_SUCCESS.getMessage(), budgetService.getAllBudgets(userId)));
  }

  /**
   * Creates a new budget for the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @param dto    Budget creation request details.
   * @return The created budget details.
   */
  @Operation(
    summary = "Create a new budget",
    description = "Allows the logged-in user to create a personal budget under a specific expense category."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Budget created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid budget details provided"),
    @ApiResponse(responseCode = "404", description = "User or Category not found"),
    @ApiResponse(responseCode = "409", description = "Overlapping budget already exists")
  })
  @PostMapping
  public ResponseEntity<CustomResponse<BudgetResponseDTO>> create(@RequestAttribute("userId") Long userId, @RequestBody BudgetRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_CREATED_SUCCESS.getMessage(), budgetService.createBudget(userId, dto)));
  }

  /**
   * Updates an existing budget belonging to the user.
   *
   * @param userId   ID of the authenticated user (injected via JWT filter).
   * @param budgetId ID of the budget to update.
   * @param dto      Updated budget details.
   * @return Updated budget details.
   */
  @Operation(
    summary = "Update budget details",
    description = "Updates an existing personal budget’s details such as amount, dates, or category."
  )
  @Parameter(name = "budgetId", description = "Unique identifier of the budget to update", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid details or date range provided"),
    @ApiResponse(responseCode = "404", description = "Budget, User, or Category not found"),
    @ApiResponse(responseCode = "409", description = "Overlapping budget exists for the updated range")
  })
  @PutMapping("/{budgetId}")
  public ResponseEntity<CustomResponse<BudgetResponseDTO>> update(@RequestAttribute("userId") Long userId, @PathVariable("budgetId") Long budgetId, @RequestBody BudgetRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_UPDATED_SUCCESS.getMessage(), budgetService.updateBudget(userId, budgetId, dto)));
  }

  /**
   * Deletes an existing budget owned by the user.
   *
   * @param userId   ID of the authenticated user (injected via JWT filter).
   * @param budgetId ID of the budget to delete.
   * @return Success message after deletion.
   */
  @Operation(
    summary = "Delete a budget",
    description = "Deletes a personal budget owned by the authenticated user."
  )
  @Parameter(name = "budgetId", description = "Unique identifier of the budget to delete", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Budget or User not found")
  })
  @DeleteMapping("/{budgetId}")
  public ResponseEntity<CustomResponseMessage> delete(@RequestAttribute("userId") Long userId, @PathVariable("budgetId") Long budgetId) {
    budgetService.deleteBudget(userId, budgetId);
    return ResponseEntity.ok(new CustomResponseMessage(true, BudgetResponseMessage.BUDGET_DELETED_SUCCESS.getMessage()));
  }

  /**
   * Provides spending analytics for all user budgets.
   * Includes comparisons between budgeted and actual spending amounts.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @return List of analytics including spending summary for each budget.
   */
  @Operation(
    summary = "Get budget analytics",
    description = "Provides spending analytics for all budgets, including budgeted vs actual spending, remaining balance, and usage percentage."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget analytics fetched successfully"),
    @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/analysis")
  public ResponseEntity<CustomResponse<List<BudgetAnalyticsDTO>>> getBudgetAnalytics(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, BudgetResponseMessage.BUDGET_ANALYSIS_FETCH_SUCCESS.getMessage(), budgetService.getBudgetAnalytics(userId)));
  }
}