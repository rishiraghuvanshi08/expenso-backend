package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.category.CategoryDetailResponseDTO;
import com.expenso.Expenso.dto.category.CategoryResponseDTO;
import com.expenso.Expenso.dto.category.CreateCategoryRequestDTO;
import com.expenso.Expenso.dto.category.UpdateCategoryRequestDTO;
import com.expenso.Expenso.enums.response.CategoryResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing personal and group categories in the Expenso application.
 *
 * Base URL: /api/v1/categories
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category APIs", description = "Handles CRUD operations and visibility of personal and group categories")
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * Retrieves detailed information about a specific category, including
   * linked transactions (personal/group) and associated budgets.
   *
   * @param categoryId ID of the category to retrieve details for.
   * @param userId     ID of the authenticated user (injected via JWT filter).
   * @return CustomResponse containing detailed category information.
   */
  @Operation(summary = "Get category details", description = "Fetches detailed information about a specific category including transactions and budgets.")
  @Parameter(name = "categoryId", description = "Unique identifier of the category", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Category details fetched successfully"),
    @ApiResponse(responseCode = "403", description = "Category inaccessible to user"),
    @ApiResponse(responseCode = "404", description = "Category not found")
  })
  @GetMapping("/{categoryId}")
  public ResponseEntity<CustomResponse<CategoryDetailResponseDTO>> getCategoryDetails(@PathVariable Long categoryId, @RequestAttribute Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_FETCH_SUCCESS.getMessage(), categoryService.getCategoryDetails(categoryId, userId)));
  }

  /**
   * Fetches the list of default system-defined personal categories.
   *
   * @return List of default personal categories.
   */
  @Operation(summary = "Get default personal categories", description = "Fetches system-defined personal categories available for all users.")
  @ApiResponse(responseCode = "200", description = "Default personal categories fetched successfully")
  @GetMapping("/personal/defaults")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getDefaultPersonalCategories() {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_PERSONAL_DEFAULT_FETCH_SUCCESS.getMessage(), categoryService.getDefaultPersonalCategories()));
  }

  /**
   * Retrieves all user-created personal categories for the authenticated user.
   *
   * @param userId ID of the authenticated user.
   * @return List of user-created personal categories.
   */
  @Operation(summary = "Get user-created personal categories", description = "Fetches custom personal categories created by the logged-in user.")
  @ApiResponse(responseCode = "200", description = "User-created personal categories fetched successfully")
  @GetMapping("/personal/custom")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getUserCreatedPersonalCategories(@RequestAttribute Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_PERSONAL_USER_CREATED_FETCH_SUCCESS.getMessage(), categoryService.getUserCreatedPersonalCategories(userId)));
  }

  /**
   * Fetches the list of default system-defined group categories.
   *
   * @return List of default group categories.
   */
  @Operation(summary = "Get default group categories", description = "Fetches system-defined categories applicable to all groups.")
  @ApiResponse(responseCode = "200", description = "Default group categories fetched successfully")
  @GetMapping("/group/default")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getDefaultGroupCategories() {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_GROUP_DEFAULT_FETCH_SUCCESS.getMessage(), categoryService.getDefaultGroupCategories()));
  }

  /**
   * Retrieves all custom categories created within a specific group.
   *
   * @param groupId ID of the group.
   * @return List of custom group categories.
   */
  @Operation(summary = "Get custom group categories", description = "Fetches user-created custom categories specific to a given group.")
  @Parameter(name = "groupId", description = "Unique identifier of the group", required = true)
  @ApiResponse(responseCode = "200", description = "Custom group categories fetched successfully")
  @GetMapping("/group/{groupId}/custom")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getUserCreatedGroupCategories(@PathVariable Long groupId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_GROUP_USER_CREATED_FETCH_SUCCESS.getMessage(), categoryService.getUserCreatedGroupCategories(groupId)));
  }

  /**
   * Fetches all visible categories for a user, combining both
   * default and user-created personal categories.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @return List of all visible categories for the user.
   */
  @Operation(summary = "Get all visible categories", description = "Fetches all categories visible to the user, including defaults and custom ones.")
  @ApiResponse(responseCode = "200", description = "All visible categories fetched successfully")
  @GetMapping("/all/visible")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getAllVisibleCategories(@RequestAttribute Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_VISIBLE_FETCH_SUCCESS.getMessage(), categoryService.getAllVisibleCategories(userId)));
  }

  /**
   * Fetches a merged list of group categories that includes
   * both default and group-specific custom categories.
   *
   * @param groupId ID of the group.
   * @return List of merged categories.
   */
  @Operation(summary = "Get merged group categories", description = "Fetches combined list of default and custom categories available in a specific group.")
  @Parameter(name = "groupId", description = "Unique identifier of the group", required = true)
  @ApiResponse(responseCode = "200", description = "Merged group categories fetched successfully")
  @GetMapping("/group/{groupId}/all")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getMergedGroupCategories(@PathVariable Long groupId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_GROUP_COMBINED_FETCH_SUCCESS.getMessage(), categoryService.getMergedGroupCategories(groupId)));
  }

  /**
   * Creates a new personal category for the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @param dto    Details of the category to be created.
   * @return Newly created category details.
   */
  @Operation(summary = "Create personal category", description = "Creates a new personal category for the authenticated user.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Personal category created successfully"),
    @ApiResponse(responseCode = "404", description = "Resource user not found"),
    @ApiResponse(responseCode = "409", description = "Category with same name/type already exists")
  })
  @PostMapping("/personal")
  public ResponseEntity<CustomResponse<CategoryResponseDTO>> createPersonal(@RequestAttribute Long userId, @Valid @RequestBody CreateCategoryRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_CREATED_SUCCESS.getMessage(), categoryService.createPersonalCategory(userId, dto)));
  }

  /**
   * Creates a new custom category within a specific group.
   *
   * @param groupId ID of the target group.
   * @param userId  ID of the authenticated user (injected via JWT filter).
   * @param dto     Details of the category to be created.
   * @return Newly created group category details.
   */
  @Operation(summary = "Create group category", description = "Creates a new custom category within a specific group.")
  @Parameters({
    @Parameter(name = "groupId", description = "Group identifier", required = true)
  })
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Group category created successfully"),
    @ApiResponse(responseCode = "404", description = "Resource user or group not found"),
    @ApiResponse(responseCode = "409", description = "Category with same name/type already exists")
  })
  @PostMapping("/group/{groupId}")
  public ResponseEntity<CustomResponse<CategoryResponseDTO>> createGroup(@PathVariable Long groupId, @RequestAttribute Long userId, @Valid @RequestBody CreateCategoryRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_CREATED_SUCCESS.getMessage(), categoryService.createGroupCategory(groupId, userId, dto)));
  }

  /**
   * Updates an existing category (personal or group) belonging to the authenticated user.
   *
   * @param categoryId ID of the category to update.
   * @param userId     ID of the authenticated user (injected via JWT filter).
   * @param dto        Updated category details.
   * @return Updated category details.
   */
  @Operation(summary = "Update category", description = "Updates details of an existing category owned by the user or group.")
  @Parameter(name = "categoryId", description = "Unique identifier of the category", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Category updated successfully"),
    @ApiResponse(responseCode = "404", description = "Category not found"),
    @ApiResponse(responseCode = "409", description = "Category update restricted or same category already exists")
  })
  @PutMapping("/{categoryId}")
  public ResponseEntity<CustomResponse<CategoryResponseDTO>> update(@PathVariable Long categoryId, @RequestAttribute Long userId, @Valid @RequestBody UpdateCategoryRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_UPDATED_SUCCESS.getMessage(), categoryService.updateCategory(categoryId, userId, dto)));
  }

  /**
   * Deletes a category owned by the authenticated user.
   *
   * @param categoryId ID of the category to delete.
   * @param userId     ID of the authenticated user (injected via JWT filter).
   * @return CustomResponseMessage indicating successful deletion.
   */
  @Operation(summary = "Delete category", description = "Deletes a category if it belongs to the authenticated user or user's group.")
  @Parameter(name = "categoryId", description = "Unique identifier of the category", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
    @ApiResponse(responseCode = "400", description = "Category deletion restricted or failed because category is in use"),
    @ApiResponse(responseCode = "404", description = "Category not found")
  })
  @DeleteMapping("/{categoryId}")
  public ResponseEntity<CustomResponseMessage> delete(@PathVariable Long categoryId, @RequestAttribute Long userId) {
    categoryService.deleteCategory(categoryId, userId);
    return ResponseEntity.ok((new CustomResponseMessage(true, CategoryResponseMessage.CATEGORY_DELETED_SUCCESS.getMessage())));
  }
}