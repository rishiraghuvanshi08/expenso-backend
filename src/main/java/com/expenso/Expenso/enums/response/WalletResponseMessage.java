package com.expenso.Expenso.enums.response;

public enum WalletResponseMessage {

  // Create Wallet
  WALLET_CREATED_SUCCESS("Wallet created successfully."),
  WALLET_CREATION_FAILED("Failed to create wallet."),
  WALLET_ALREADY_EXISTS("A wallet with the same name and type already exists."),

  // Update Wallet
  WALLET_UPDATED_SUCCESS("Wallet updated successfully."),
  WALLET_UPDATE_FAILED("Failed to update wallet."),

  // Delete Wallet
  WALLET_DELETED_SUCCESS("Wallet deleted successfully."),
  WALLET_DELETION_FAILED("Failed to delete wallet."),
  WALLET_ALREADY_DELETED("Wallet is already deleted."),
  WALLET_DELETION_RESTRICTED("Wallet deletion failed, Cannot delete wallet with existing transactions."),

  // Fetch Wallets
  WALLET_FETCH_SUCCESS("Wallet fetched successfully."),
  WALLET_NOT_FOUND("Wallet not found."),
  WALLET_LIST_FETCH_SUCCESS("Wallets fetched successfully."),
  WALLET_TOTAL_BALANCE_FETCH_SUCCESS("Total balance fetched successfully");

  private final String message;

  WalletResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}