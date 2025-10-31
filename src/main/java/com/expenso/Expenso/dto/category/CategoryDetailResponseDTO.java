package com.expenso.Expenso.dto.category;

import com.expenso.Expenso.dto.grouptransaction.GroupTransactionDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed category response including related transactions and budgets")
public class CategoryDetailResponseDTO {

  @Schema(description = "Unique category ID", example = "201")
  private Long categoryId;

  @Schema(description = "Category name", example = "Utilities")
  private String name;

  @Schema(description = "Type of the category (INCOME or EXPENSE)", example = "EXPENSE")
  private CategoryType categoryType;

  @Schema(description = "Scope of the category (PERSONAL or GROUP)", example = "GROUP")
  private CategoryScope categoryScope;

  @Schema(description = "List of user transactions linked to this category")
  private List<UserTransactionResponseDTO> userTransactions;

  @Schema(description = "List of group transactions linked to this category")
  private List<GroupTransactionDTO> groupTransactions;

  @Schema(description = "List of active or historical budgets assigned to this category")
  private List<CategoryBudgetInfoDTO> budgets;
}