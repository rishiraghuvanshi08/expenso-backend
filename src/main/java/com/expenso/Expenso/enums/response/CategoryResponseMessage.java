package com.expenso.Expenso.enums.response;

public enum CategoryResponseMessage {

  // Create Category
  CATEGORY_CREATED_SUCCESS("Category created successfully."),
  CATEGORY_CREATION_FAILED("Failed to create category."),

  // Update Category
  CATEGORY_UPDATED_SUCCESS("Category updated successfully."),
  CATEGORY_UPDATE_FAILED("Failed to update category."),

  // Delete Category
  CATEGORY_DELETED_SUCCESS("Category deleted successfully."),
  CATEGORY_DELETION_FAILED("Failed to delete category."),
  CATEGORY_ALREADY_DELETED("Category is already deleted."),

  // Fetch Categories
  CATEGORY_FETCH_SUCCESS("Category fetched successfully."),
  CATEGORY_NOT_FOUND("Category not found."),
  CATEGORY_LIST_FETCH_SUCCESS("Categories fetched successfully.");

  private final String message;

  CategoryResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}