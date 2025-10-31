package com.expenso.Expenso.dto.grouptransaction;

import com.expenso.Expenso.enums.entity.GroupTransactionSplitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed information about a group transaction, including amount, participants, and settlement status.")
public class GroupTransactionDTO {

  @Schema(description = "Unique identifier of the group transaction.", example = "101")
  private Long id;

  @Schema(description = "Total amount of the transaction.", example = "2500.00")
  private BigDecimal amount;

  @Schema(description = "Optional note or description for the transaction.", example = "Dinner with friends at Olive Garden")
  private String note;

  @Schema(description = "Indicates whether the group transaction has been fully settled.", example = "false")
  private boolean settled;

  @Schema(description = "Date when the group transaction occurred.", example = "2025-10-28")
  private LocalDate date;

  @Schema(description = "Type of split applied to the transaction (e.g., EQUAL, UNEQUAL, PERCENTAGE).", example = "EQUAL")
  private GroupTransactionSplitType splitType;

  @Schema(description = "Identifier of the associated expense group.", example = "12")
  private Long expenseGroupId;

  @Schema(description = "Name of the group where this expense belongs.", example = "Weekend Trip to Miami")
  private String groupName;

  @Schema(description = "Identifier of the user who paid for this transaction.", example = "7")
  private Long paidByUserId;

  @Schema(description = "Full name of the user who paid for this transaction.", example = "Alice Johnson")
  private String paidByUserName;
}