package com.expenso.Expenso.enums.response;

public enum ExpenseGroupResponseMessage {

  // Create Group
  GROUP_CREATED_SUCCESS("Expense group created successfully."),
  GROUP_CREATION_FAILED("Failed to create expense group."),

  // Update Group
  GROUP_UPDATED_SUCCESS("Expense group updated successfully."),
  GROUP_UPDATE_FAILED("Failed to update expense group."),

  // Delete Group
  GROUP_DELETED_SUCCESS("Expense group deleted successfully."),
  GROUP_DELETION_FAILED("Failed to delete expense group."),
  GROUP_ALREADY_DELETED("Expense group is already deleted."),

  // Fetch Group
  GROUP_FETCH_SUCCESS("Expense group fetched successfully."),
  GROUP_LIST_FETCH_SUCCESS("Expense groups fetched successfully."),
  GROUP_NOT_FOUND("Expense group not found."),

  // Membership
  GROUP_MEMBER_ADDED_SUCCESS("Member added to the expense group successfully."),
  GROUP_MEMBER_REMOVED_SUCCESS("Member removed from the expense group successfully."),
  GROUP_MEMBER_ALREADY_EXISTS("User is already a member of the expense group."),
  GROUP_MEMBER_NOT_FOUND("User is not a member of the expense group."),
  GROUP_JOIN_REQUEST_SENT("Join request sent successfully."),
  GROUP_JOIN_REQUEST_ACCEPTED("Join request accepted successfully."),
  GROUP_JOIN_REQUEST_REJECTED("Join request rejected."),
  GROUP_INVITATION_ALREADY_SENT("An invitation to this user has already been sent."),
  GROUP_INVITATION_NOT_FOUND("No pending invitation found for this user."),

  // Visibility and Access
  GROUP_ACCESS_DENIED("Access denied. You are not authorized to view or modify this group."),
  GROUP_VISIBILITY_RESTRICTED("You are not authorized to access this expense group."),

  // Conflict and Duplicates
  GROUP_NAME_ALREADY_EXISTS("An expense group with the same name already exists."),
  GROUP_NAME_CONFLICT("Group name conflict detected."),

  // Stats and Insights (for future features)
  GROUP_STATS_FETCH_SUCCESS("Group statistics fetched successfully."),
  GROUP_SUMMARY_FETCH_SUCCESS("Group summary fetched successfully."),
  GROUP_EXPENSE_OVERVIEW_SUCCESS("Group expense overview fetched successfully."),

  // Miscellaneous
  GROUP_OPERATION_FAILED("Operation on expense group failed. Please try again later."),
  GROUP_ACTION_NOT_ALLOWED("Action not allowed on this expense group.");

  private final String message;

  ExpenseGroupResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}