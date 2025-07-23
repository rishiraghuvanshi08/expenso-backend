package com.expenso.Expenso.constants.response;

public enum AppUserResponseMessage {

  // Get User
  USER_FETCH_SUCCESS("User fetched successfully."),
  USER_NOT_FOUND("User not found."),

  // Update User
  USER_UPDATE_SUCCESS("User updated successfully."),
  USER_UPDATE_FAILED("Failed to update user."),

  // Deactivate User
  USER_DEACTIVATION_SUCCESS("User deactivated successfully."),
  USER_ALREADY_DEACTIVATED("User is already deactivated."),
  USER_DEACTIVATION_FAILED("Failed to deactivate user."),

  // Change Password
  PASSWORD_CHANGE_SUCCESS("Password changed successfully."),
  PASSWORD_MISMATCH("Current password does not match."),
  PASSWORD_CHANGE_FAILED("Failed to change password.");

  ;

  private final String message;

  AppUserResponseMessage(String message){
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
