package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.category.CategoryDetailResponseDTO;
import com.expenso.Expenso.dto.category.CategoryResponseDTO;
import com.expenso.Expenso.dto.category.CreateCategoryRequestDTO;
import com.expenso.Expenso.dto.category.UpdateCategoryRequestDTO;

import java.util.List;

public interface CategoryService {
  CategoryDetailResponseDTO getCategoryDetails(Long categoryId, Long userId);

  // Personal
  List<CategoryResponseDTO> getDefaultPersonalCategories();
  List<CategoryResponseDTO> getUserCreatedPersonalCategories(Long userId);

  // Group
  List<CategoryResponseDTO> getDefaultGroupCategories();
  List<CategoryResponseDTO> getUserCreatedGroupCategories(Long groupId);

  // Combined
  List<CategoryResponseDTO> getAllVisibleCategories(Long userId);
  List<CategoryResponseDTO> getMergedGroupCategories(Long groupId);

  CategoryResponseDTO createPersonalCategory(Long userId, CreateCategoryRequestDTO dto);

  CategoryResponseDTO createGroupCategory(Long groupId, Long userId, CreateCategoryRequestDTO dto);

  CategoryResponseDTO updateCategory(Long categoryId, Long userId, UpdateCategoryRequestDTO dto);

  void deleteCategory(Long categoryId, Long userId);
}