package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.category.CategoryDetailResponseDTO;
import com.expenso.Expenso.dto.category.CategoryResponseDTO;
import com.expenso.Expenso.dto.category.CreateCategoryRequestDTO;
import com.expenso.Expenso.dto.category.UpdateCategoryRequestDTO;

import java.util.List;

/**
 * Service interface for managing categories across personal and group scopes.
 * Provides operations for creating, updating, deleting, and fetching category details,
 * including associated transactions and budgets.
 */
public interface CategoryService {

  /**
   * Retrieves detailed information about a category, including related
   * personal transactions, group transactions, and budgets.
   *
   * @param categoryId ID of the category to fetch.
   * @param userId     ID of the requesting user.
   * @return Category details wrapped in {@link CategoryDetailResponseDTO}.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if the category is not found.
   * @throws com.expenso.Expenso.exception.custom.AccessDeniedException if the user is not authorized to access this category.
   */
  CategoryDetailResponseDTO getCategoryDetails(Long categoryId, Long userId);

  /**
   * Fetches all default personal categories available for every user.
   *
   * @return List of default personal categories.
   */
  List<CategoryResponseDTO> getDefaultPersonalCategories();

  /**
   * Fetches all custom personal categories created by a specific user.
   *
   * @param userId ID of the user whose categories are to be fetched.
   * @return List of user-created personal categories.
   */
  List<CategoryResponseDTO> getUserCreatedPersonalCategories(Long userId);

  /**
   * Fetches all default group categories available for every group.
   *
   * @return List of default group categories.
   */
  List<CategoryResponseDTO> getDefaultGroupCategories();

  /**
   * Fetches all custom group categories created within a specific group.
   *
   * @param groupId ID of the group.
   * @return List of user-created group categories.
   */
  List<CategoryResponseDTO> getUserCreatedGroupCategories(Long groupId);

  /**
   * Fetches all visible categories for a specific user,
   * combining default and user-created personal categories.
   *
   * @param userId ID of the user.
   * @return List of visible categories for the user.
   */
  List<CategoryResponseDTO> getAllVisibleCategories(Long userId);

  /**
   * Fetches merged list of categories for a group,
   * combining both default and user-created group categories.
   *
   * @param groupId ID of the group.
   * @return List of combined group categories.
   */
  List<CategoryResponseDTO> getMergedGroupCategories(Long groupId);

  /**
   * Creates a new personal category under the specified user.
   *
   * @param userId ID of the user creating the category.
   * @param dto    Category creation details.
   * @return Created category as {@link CategoryResponseDTO}.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if user is not found.
   * @throws com.expenso.Expenso.exception.custom.ResourceAlreadyExistsException if category with same name/type already exists.
   */
  CategoryResponseDTO createPersonalCategory(Long userId, CreateCategoryRequestDTO dto);

  /**
   * Creates a new group category within a specified group.
   *
   * @param groupId ID of the expense group.
   * @param userId  ID of the user creating the category.
   * @param dto     Category creation details.
   * @return Created category as {@link CategoryResponseDTO}.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if group or user is not found.
   * @throws com.expenso.Expenso.exception.custom.ResourceAlreadyExistsException if category with same name/type already exists in the group.
   */
  CategoryResponseDTO createGroupCategory(Long groupId, Long userId, CreateCategoryRequestDTO dto);

  /**
   * Updates an existing category's name or type.
   *
   * @param categoryId ID of the category to update.
   * @param userId     ID of the user performing the update.
   * @param dto        Updated category details.
   * @return Updated category as {@link CategoryResponseDTO}.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if category does not exist.
   * @throws com.expenso.Expenso.exception.custom.UpdationFailedException if user lacks permission or category type cannot be changed.
   * @throws com.expenso.Expenso.exception.custom.ResourceAlreadyExistsException if duplicate category name/type exists.
   */
  CategoryResponseDTO updateCategory(Long categoryId, Long userId, UpdateCategoryRequestDTO dto);

  /**
   * Deletes a category if not used in any transactions or restricted by system rules.
   *
   * @param categoryId ID of the category to delete.
   * @param userId     ID of the requesting user.
   * @throws com.expenso.Expenso.exception.custom.ResourceNotFoundException if category not found.
   * @throws com.expenso.Expenso.exception.custom.DeletionFailedException if deletion is restricted or category is in use.
   */
  void deleteCategory(Long categoryId, Long userId);
}