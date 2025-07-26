package com.expenso.Expenso.enums.response;

public enum UserTransactionResponseMessage {

  // Create Transaction
  TRANSACTION_CREATED_SUCCESS("Transaction created successfully."),
  TRANSACTION_CREATION_FAILED("Failed to create transaction."),

  // Update Transaction
  TRANSACTION_UPDATED_SUCCESS("Transaction updated successfully."),
  TRANSACTION_UPDATE_FAILED("Failed to update transaction."),

  // Delete Transaction
  TRANSACTION_DELETED_SUCCESS("Transaction deleted successfully."),
  TRANSACTION_DELETION_FAILED("Failed to delete transaction."),
  TRANSACTION_ALREADY_DELETED("Transaction is already deleted."),

  // Fetch Transactions
  TRANSACTION_FETCH_SUCCESS("Transaction fetched successfully."),
  TRANSACTION_NOT_FOUND("Transaction not found."),
  TRANSACTION_LIST_FETCH_SUCCESS("Transactions fetched successfully."),

  // Summary & Stats
  TRANSACTION_SUMMARY_FETCH_SUCCESS("Transaction summary fetched successfully."),
  TRANSACTION_STATS_FETCH_SUCCESS("Transaction statistics fetched successfully."),
  TRANSACTION_FILTER_RESULT_SUCCESS("Filtered transactions fetched successfully.");

  private final String message;

  UserTransactionResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
