package com.expenso.Expenso.dto.category;

import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {

  private Long id;
  private String name;
  private CategoryType categoryType;
  private CategoryScope categoryScope;
  private Long userId;
  private Long groupId;
}