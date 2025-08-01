package com.expenso.Expenso.dto.category;

import com.expenso.Expenso.dto.budget.BudgetDTO;
import com.expenso.Expenso.dto.grouptransaction.GroupTransactionDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponseDTO {
  private Long categoryId;
  private String name;
  private CategoryType categoryType;
  private CategoryScope categoryScope;

  private List<UserTransactionResponseDTO> userTransactions;
  private List<GroupTransactionDTO> groupTransactions;
  private List<BudgetDTO> budgets;
}