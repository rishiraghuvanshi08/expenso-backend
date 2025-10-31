package com.expenso.Expenso.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents budget information linked to a category")
public class CategoryBudgetInfoDTO {

  @Schema(description = "Unique budget ID", example = "501")
  private Long id;

  @Schema(description = "Allocated budget amount for the category", example = "1500.00")
  private BigDecimal amount;

  @Schema(description = "Start date of the budget period", example = "2025-01-01")
  private LocalDate startDate;

  @Schema(description = "End date of the budget period", example = "2025-01-31")
  private LocalDate endDate;

  @Schema(description = "Indicates if the budget is currently active", example = "true")
  private boolean active;
}