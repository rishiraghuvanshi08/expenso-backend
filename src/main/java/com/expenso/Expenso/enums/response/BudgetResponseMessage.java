package com.expenso.Expenso.enums.response;

public enum BudgetResponseMessage {

  // Create Budget
  BUDGET_CREATED_SUCCESS("Budget created successfully."),
  BUDGET_CREATION_FAILED("Failed to create budget."),

  // Update Budget
  BUDGET_UPDATED_SUCCESS("Budget updated successfully."),
  BUDGET_UPDATE_FAILED("Failed to update budget."),

  // Delete Budget
  BUDGET_DELETED_SUCCESS("Budget deleted successfully."),
  BUDGET_DELETION_FAILED("Failed to delete budget."),
  BUDGET_ALREADY_DELETED("Budget is already deleted."),

  // Fetch Budgets
  BUDGET_FETCH_SUCCESS("Budget fetched successfully."),
  BUDGET_NOT_FOUND("Budget not found."),
  BUDGET_LIST_FETCH_SUCCESS("Budgets fetched successfully."),

  // Validation Errors
  INVALID_BUDGET_AMOUNT("Budget amount must be greater than zero."),
  INVALID_START_DATE("Start date must be today or a future date."),
  INVALID_DATE_RANGE("End date must be after or equal to start date."),
  START_DATE_AFTER_END_DATE("Start date must be before or equal to end date."),
  INVALID_CATEGORY_TYPE("Only EXPENSE categories allowed for budgets."),

  // Conflict Errors
  DUPLICATE_BUDGET_FOR_CATEGORY("A budget already exists for this category."),
  OVERLAPPING_BUDGET_EXISTS("A budget already exists for this category in the given date range."),
  OVERLAPPING_BUDGET_ON_UPDATE("Another budget exists for the same category in overlapping date range."),
  INVALID_CATEGORY_SCOPE("Only personal scoped categories are allowed for budgeting."),
  UNAUTHORIZED_CUSTOM_CATEGORY("You can only use your own custom categories for budgeting."),

  // Budget Summary & Analysis
  BUDGET_ANALYSIS_FETCH_SUCCESS("Budget analysis fetched successfully."),
  BUDGET_FILTER_RESULT_SUCCESS("Filtered budgets fetched successfully."),
  BUDGET_STATUS_FETCH_SUCCESS("Budget status fetched successfully.");

  private final String message;

  BudgetResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
