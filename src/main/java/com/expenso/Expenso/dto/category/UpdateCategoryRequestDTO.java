package com.expenso.Expenso.dto.category;

import com.expenso.Expenso.enums.entity.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating an existing category")
public class UpdateCategoryRequestDTO {

  @Schema(description = "Updated category name", example = "Entertainment")
  private String name;

  @Schema(description = "Updated category type (INCOME or EXPENSE)", example = "EXPENSE")
  private CategoryType categoryType;
}