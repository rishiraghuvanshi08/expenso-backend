package com.expenso.Expenso.dto.category;

import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new category")
public class CreateCategoryRequestDTO {
  @NotBlank
  @Schema(description = "Name of the new category", example = "Travel")
  private String name;

  @NotNull
  @Schema(description = "Type of the category (INCOME or EXPENSE)", example = "EXPENSE")
  private CategoryType categoryType;

}