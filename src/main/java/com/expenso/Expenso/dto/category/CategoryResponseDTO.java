package com.expenso.Expenso.dto.category;

import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a category entity, either personal or group-scoped")
public class CategoryResponseDTO {

  @Schema(description = "Unique category ID", example = "201")
  private Long id;

  @Schema(description = "Category name", example = "Groceries")
  private String name;

  @Schema(description = "Type of the category (INCOME or EXPENSE)", example = "EXPENSE")
  private CategoryType categoryType;

  @Schema(description = "Scope of the category (PERSONAL or GROUP)", example = "PERSONAL")
  private CategoryScope categoryScope;

  @Schema(description = "Associated user ID (for personal categories)", example = "101")
  private Long userId;

  @Schema(description = "Associated group ID (for group categories)", example = "301")
  private Long groupId;
}