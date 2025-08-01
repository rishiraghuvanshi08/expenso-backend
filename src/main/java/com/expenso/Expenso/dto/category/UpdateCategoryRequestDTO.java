package com.expenso.Expenso.dto.category;

import com.expenso.Expenso.enums.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequestDTO {
  private String name;
  private CategoryType categoryType;
}