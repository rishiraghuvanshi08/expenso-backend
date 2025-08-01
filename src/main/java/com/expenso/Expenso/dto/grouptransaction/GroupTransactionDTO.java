package com.expenso.Expenso.dto.grouptransaction;

import com.expenso.Expenso.enums.entity.GroupTransactionSplitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTransactionDTO {
  private Long id;
  private BigDecimal amount;
  private String note;
  private boolean settled;
  private LocalDate date;
  private GroupTransactionSplitType splitType;
  private Long expenseGroupId;
  private String groupName;
  private Long paidByUserId;
  private String paidByUserName;
}