package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.category.CategoryDetailResponseDTO;
import com.expenso.Expenso.dto.category.CategoryResponseDTO;
import com.expenso.Expenso.dto.category.CreateCategoryRequestDTO;
import com.expenso.Expenso.dto.category.UpdateCategoryRequestDTO;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Category;
import com.expenso.Expenso.entities.ExpenseGroup;
import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import com.expenso.Expenso.enums.response.AppUserResponseMessage;
import com.expenso.Expenso.enums.response.CategoryResponseMessage;
import com.expenso.Expenso.exception.custom.*;
import com.expenso.Expenso.repository.*;
import com.expenso.Expenso.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private ExpenseGroupRepository expenseGroupRepository;
  @Mock private UserTransactionRepository userTransactionRepository;
  @Mock private GroupTransactionRepository groupTransactionRepository;
  @Mock private BudgetRepository budgetRepository;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private Category buildCategory() {
    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    category.setCategoryType(CategoryType.EXPENSE);
    category.setCategoryScope(CategoryScope.PERSONAL);
    return category;
  }

  private AppUser buildUser() {
    return AppUser.builder()
                  .id(1L)
                  .name("Rishi")
                  .email("rishi@gmail.com")
                  .build();
  }

  // ================= READ TESTS =================

  @Test
  void getDefaultPersonalCategories_shouldReturnList() {
    when(categoryRepository.findDefaultPersonalCategories())
      .thenReturn(List.of(buildCategory()));

    List<CategoryResponseDTO> result =
      categoryService.getDefaultPersonalCategories();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Food");
  }

  @Test
  void getDefaultGroupCategories_shouldReturnList() {
    when(categoryRepository.findDefaultGroupCategories())
      .thenReturn(List.of(buildCategory()));

    List<CategoryResponseDTO> result =
      categoryService.getDefaultGroupCategories();

    assertThat(result).hasSize(1);
  }

  @Test
  void getUserCreatedPersonalCategories_shouldReturnList() {
    when(categoryRepository.findUserCreatedPersonalCategories(1L))
      .thenReturn(List.of(buildCategory()));

    List<CategoryResponseDTO> result =
      categoryService.getUserCreatedPersonalCategories(1L);

    assertThat(result).hasSize(1);
  }

  @Test
  void getMergedGroupCategories_shouldReturnList() {
    when(categoryRepository.findMergedGroupCategories(1L))
      .thenReturn(List.of(buildCategory()));

    List<CategoryResponseDTO> result =
      categoryService.getMergedGroupCategories(1L);

    assertThat(result).hasSize(1);
  }

  // ================= CREATE PERSONAL =================

  @Test
  void createPersonalCategory_shouldCreateSuccessfully() {
    AppUser user = buildUser();

    CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO();
    dto.setName("Travel");
    dto.setCategoryType(CategoryType.EXPENSE);

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(categoryRepository
           .existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(
             anyString(), any(), any(), anyLong()))
      .thenReturn(false);

    Category savedCategory = new Category();
    savedCategory.setId(1L);
    savedCategory.setName("Travel");
    savedCategory.setCategoryType(CategoryType.EXPENSE);
    savedCategory.setCategoryScope(CategoryScope.PERSONAL);
    savedCategory.setAppUser(user);

    when(categoryRepository.save(any(Category.class)))
      .thenReturn(savedCategory);

    CategoryResponseDTO response =
      categoryService.createPersonalCategory(1L, dto);

    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Travel");
  }

  @Test
  void createPersonalCategory_shouldThrow_whenDuplicateExists() {
    AppUser user = buildUser();

    CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO();
    dto.setName("Food");
    dto.setCategoryType(CategoryType.EXPENSE);

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

    when(categoryRepository
           .existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(
             anyString(), any(), any(), anyLong()))
      .thenReturn(true);

    assertThatThrownBy(() ->
                         categoryService.createPersonalCategory(1L, dto))
      .isInstanceOf(ResourceAlreadyExistsException.class)
      .hasMessageContaining(
        CategoryResponseMessage.CATEGORY_ALREADY_EXISTS.getMessage()
      );
  }

  @Test
  void createPersonalCategory_shouldThrow_whenUserNotFound() {
    CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO();

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         categoryService.createPersonalCategory(1L, dto))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(
        AppUserResponseMessage.USER_NOT_FOUND.getMessage()
      );
  }

  // ================= CREATE GROUP =================

  @Test
  void createGroupCategory_shouldCreateSuccessfully() {
    AppUser user = buildUser();

    ExpenseGroup group = new ExpenseGroup();
    group.setId(10L);
    group.setName("Trip");

    CreateCategoryRequestDTO dto = new CreateCategoryRequestDTO();
    dto.setName("Stay");
    dto.setCategoryType(CategoryType.EXPENSE);

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(expenseGroupRepository.findById(10L))
      .thenReturn(Optional.of(group));

    when(categoryRepository
           .existsByNameAndCategoryTypeAndCategoryScopeAndExpenseGroupId(
             anyString(), any(), any(), anyLong()))
      .thenReturn(false);

    Category saved = new Category();
    saved.setId(1L);
    saved.setName("Stay");
    saved.setExpenseGroup(group);
    saved.setAppUser(user);
    saved.setCategoryScope(CategoryScope.GROUP);

    when(categoryRepository.save(any(Category.class)))
      .thenReturn(saved);

    CategoryResponseDTO response =
      categoryService.createGroupCategory(10L, 1L, dto);

    assertThat(response.getName()).isEqualTo("Stay");
    assertThat(response.getGroupId()).isEqualTo(10L);
  }

  // ================= UPDATE CATEGORY =================

  @Test
  void updateCategory_shouldUpdateSuccessfully_whenValidRequest() {
    AppUser user = buildUser();

    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    category.setCategoryType(CategoryType.EXPENSE);
    category.setCategoryScope(CategoryScope.PERSONAL);
    category.setAppUser(user);

    UpdateCategoryRequestDTO dto = new UpdateCategoryRequestDTO();
    dto.setName("Travel");
    dto.setCategoryType(CategoryType.EXPENSE);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(userTransactionRepository
           .existsByCategoryIdAndIsDeletedFalse(1L))
      .thenReturn(false);

    when(categoryRepository
           .existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(
             anyString(), any(), any(), anyLong()))
      .thenReturn(false);

    when(categoryRepository.save(any(Category.class)))
      .thenReturn(category);

    CategoryResponseDTO response =
      categoryService.updateCategory(1L, 1L, dto);

    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Travel");
  }

  @Test
  void updateCategory_shouldThrow_whenUserNotOwner() {
    AppUser owner = buildUser();
    owner.setId(99L);

    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    category.setCategoryScope(CategoryScope.PERSONAL);
    category.setCategoryType(CategoryType.EXPENSE);
    category.setAppUser(owner);

    UpdateCategoryRequestDTO dto = new UpdateCategoryRequestDTO();
    dto.setName("Travel");
    dto.setCategoryType(CategoryType.EXPENSE);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    assertThatThrownBy(() ->
                         categoryService.updateCategory(1L, 1L, dto))
      .isInstanceOf(UpdationFailedException.class);
  }

  @Test
  void updateCategory_shouldThrow_whenDuplicateExists() {
    AppUser user = buildUser();

    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    category.setCategoryType(CategoryType.EXPENSE);
    category.setCategoryScope(CategoryScope.PERSONAL);
    category.setAppUser(user);

    UpdateCategoryRequestDTO dto = new UpdateCategoryRequestDTO();
    dto.setName("Travel");
    dto.setCategoryType(CategoryType.EXPENSE);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(userTransactionRepository
           .existsByCategoryIdAndIsDeletedFalse(1L))
      .thenReturn(false);

    when(categoryRepository
           .existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(
             anyString(), any(), any(), anyLong()))
      .thenReturn(true);

    assertThatThrownBy(() ->
                         categoryService.updateCategory(1L, 1L, dto))
      .isInstanceOf(ResourceAlreadyExistsException.class);
  }

  // ================= DELETE CATEGORY =================

  @Test
  void deleteCategory_shouldDeleteSuccessfully() {
    AppUser user = buildUser();

    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    category.setAppUser(user);
    category.setCategoryScope(CategoryScope.PERSONAL);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(userTransactionRepository.existsByCategoryId(1L))
      .thenReturn(false);

    categoryService.deleteCategory(1L, 1L);

    verify(categoryRepository).delete(category);
  }

  @Test
  void deleteCategory_shouldThrow_whenUsedInTransaction() {
    AppUser user = buildUser();

    Category category = new Category();
    category.setId(1L);
    category.setAppUser(user);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(userTransactionRepository.existsByCategoryId(1L))
      .thenReturn(true);

    assertThatThrownBy(() ->
                         categoryService.deleteCategory(1L, 1L))
      .isInstanceOf(DeletionFailedException.class);
  }

  @Test
  void deleteCategory_shouldThrow_whenDefaultCategory() {
    Category category = new Category();
    category.setId(1L);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    assertThatThrownBy(() ->
                         categoryService.deleteCategory(1L, 1L))
      .isInstanceOf(DeletionFailedException.class);
  }

  // ================= CATEGORY DETAILS =================

  @Test
  void getCategoryDetails_shouldReturnPersonalCategoryDetails() {
    AppUser user = buildUser();

    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    category.setCategoryScope(CategoryScope.PERSONAL);
    category.setAppUser(user);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(userTransactionRepository
           .findByAppUserIdAndCategoryIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(List.of());

    when(budgetRepository.findByAppUserIdAndCategoryId(1L, 1L))
      .thenReturn(List.of());

    CategoryDetailResponseDTO response =
      categoryService.getCategoryDetails(1L, 1L);

    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Food");
  }

  @Test
  void getCategoryDetails_shouldThrow_whenPersonalAccessDenied() {
    AppUser owner = buildUser();
    owner.setId(99L);

    Category category = new Category();
    category.setId(1L);
    category.setCategoryScope(CategoryScope.PERSONAL);
    category.setAppUser(owner);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    assertThatThrownBy(() ->
                         categoryService.getCategoryDetails(1L, 1L))
      .isInstanceOf(AccessDeniedException.class);
  }
}