package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.category.CategoryDetailResponseDTO;
import com.expenso.Expenso.dto.category.CategoryResponseDTO;
import com.expenso.Expenso.dto.category.CreateCategoryRequestDTO;
import com.expenso.Expenso.dto.category.UpdateCategoryRequestDTO;
import com.expenso.Expenso.enums.response.CategoryResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping("/{categoryId}")
  public ResponseEntity<CustomResponse<CategoryDetailResponseDTO>> getCategoryDetails(@PathVariable Long categoryId, @RequestAttribute Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_FETCH_SUCCESS.getMessage(), categoryService.getCategoryDetails(categoryId, userId)));
  }

  @GetMapping("/personal/defaults")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getDefaultPersonalCategories() {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_PERSONAL_DEFAULT_FETCH_SUCCESS.getMessage(), categoryService.getDefaultPersonalCategories()));
  }

  @GetMapping("/personal/custom")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getUserCreatedPersonalCategories(@RequestAttribute Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_PERSONAL_USER_CREATED_FETCH_SUCCESS.getMessage(), categoryService.getUserCreatedPersonalCategories(userId)));
  }

  @GetMapping("/group/default")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getDefaultGroupCategories() {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_GROUP_DEFAULT_FETCH_SUCCESS.getMessage(), categoryService.getDefaultGroupCategories()));
  }

  @GetMapping("/group/{groupId}/custom")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getUserCreatedGroupCategories(@PathVariable Long groupId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_GROUP_USER_CREATED_FETCH_SUCCESS.getMessage(), categoryService.getUserCreatedGroupCategories(groupId)));
  }

  @GetMapping("/all/visible")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getAllVisibleCategories(@RequestAttribute Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_VISIBLE_FETCH_SUCCESS.getMessage(), categoryService.getAllVisibleCategories(userId)));
  }

  @GetMapping("/group/{groupId}/all")
  public ResponseEntity<CustomResponse<List<CategoryResponseDTO>>> getMergedGroupCategories(@PathVariable Long groupId) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_GROUP_COMBINED_FETCH_SUCCESS.getMessage(), categoryService.getMergedGroupCategories(groupId)));
  }

  @PostMapping("/personal")
  public ResponseEntity<CustomResponse<CategoryResponseDTO>> createPersonal(@RequestAttribute Long userId, @Valid @RequestBody CreateCategoryRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_CREATED_SUCCESS.getMessage(), categoryService.createPersonalCategory(userId, dto)));
  }

  @PostMapping("/group/{groupId}")
  public ResponseEntity<CustomResponse<CategoryResponseDTO>> createGroup(@PathVariable Long groupId, @RequestAttribute Long userId, @Valid @RequestBody CreateCategoryRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_CREATED_SUCCESS.getMessage(), categoryService.createGroupCategory(groupId, userId, dto)));
  }

  @PutMapping("/{categoryId}")
  public ResponseEntity<CustomResponse<CategoryResponseDTO>> update(@PathVariable Long categoryId, @RequestAttribute Long userId, @Valid @RequestBody UpdateCategoryRequestDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, CategoryResponseMessage.CATEGORY_UPDATED_SUCCESS.getMessage(), categoryService.updateCategory(categoryId, userId, dto)));
  }

  @DeleteMapping("/{categoryId}")
  public ResponseEntity<CustomResponseMessage> delete(@PathVariable Long categoryId, @RequestAttribute Long userId) {
    categoryService.deleteCategory(categoryId, userId);
    return ResponseEntity.ok((new CustomResponseMessage(true, CategoryResponseMessage.CATEGORY_DELETED_SUCCESS.getMessage())));
  }
}