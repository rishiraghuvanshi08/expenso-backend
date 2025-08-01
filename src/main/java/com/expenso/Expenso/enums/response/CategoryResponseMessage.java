package com.expenso.Expenso.enums.response;

public enum CategoryResponseMessage {

  // Create Category
  CATEGORY_CREATED_SUCCESS("Category created successfully."),
  CATEGORY_CREATION_FAILED("Failed to create category."),

  // Update Category
  CATEGORY_UPDATED_SUCCESS("Category updated successfully."),
  CATEGORY_UPDATE_FAILED("Failed to update category."),
  CATEGORY_TYPE_UPDATE_RESTRICTED("Cannot change category type as it is already used in transactions."),
  CATEGORY_UPDATE_RESTRICTED("Access Denied. You're not allowed to update this category"),

  // Delete Category
  CATEGORY_DELETED_SUCCESS("Category deleted successfully."),
  CATEGORY_DELETION_FAILED("Failed to delete category."),
  CATEGORY_ALREADY_DELETED("Category is already deleted."),
  CATEGORY_DELETION_RESTRICTED("Access Denied. You're not allowed to delete this category"),

  // Fetch Categories
  CATEGORY_FETCH_SUCCESS("Category fetched successfully."),
  CATEGORY_LIST_FETCH_SUCCESS("Categories fetched successfully."),
  CATEGORY_PERSONAL_DEFAULT_FETCH_SUCCESS("Default personal categories fetched successfully."),
  CATEGORY_PERSONAL_USER_CREATED_FETCH_SUCCESS("User-created personal categories fetched successfully."),
  CATEGORY_GROUP_DEFAULT_FETCH_SUCCESS("Default group categories fetched successfully."),
  CATEGORY_GROUP_USER_CREATED_FETCH_SUCCESS("User-created group categories fetched successfully."),
  CATEGORY_VISIBLE_FETCH_SUCCESS("All visible categories fetched successfully."),
  CATEGORY_GROUP_COMBINED_FETCH_SUCCESS("Merged group categories fetched successfully."),
  CATEGORY_NOT_FOUND("Category not found."),

  // Scope and Visibility
  CATEGORY_SCOPE_MISMATCH("Mismatch between category scope and provided data."),
  CATEGORY_SCOPE_VALIDATION_FAILED("Category scope validation failed."),
  INVALID_CATEGORY_CONFIGURATION("Invalid category configuration. Check scope and ownership."),
  CATEGORY_VISIBILITY_RESTRICTED("Access denied. You are not authorized to access this category."),

  // Conflict & Duplicates
  CATEGORY_ALREADY_EXISTS("A category with the same name already exists in this scope."),
  CATEGORY_NAME_CONFLICT("Duplicate category name detected within the same scope."),

  // Filtering and Statistics (for future features)
  CATEGORY_FILTER_RESULT_SUCCESS("Filtered categories fetched successfully."),
  CATEGORY_STATS_FETCH_SUCCESS("Category statistics fetched successfully."),
  CATEGORY_SUMMARY_FETCH_SUCCESS("Category summary fetched successfully."),

  // Miscellaneous
  CATEGORY_USAGE_CONSTRAINT("Cannot delete category as it is linked to transactions or budgets."),
  CATEGORY_OPERATION_FAILED("Operation on category failed. Please try again later.");

  private final String message;

  CategoryResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}