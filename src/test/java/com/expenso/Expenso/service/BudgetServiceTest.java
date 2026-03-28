package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.budget.*;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Budget;
import com.expenso.Expenso.entities.Category;
import com.expenso.Expenso.enums.entity.*;
import com.expenso.Expenso.enums.response.BudgetResponseMessage;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.repository.*;
import com.expenso.Expenso.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

  @Mock private BudgetRepository budgetRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private UserTransactionRepository transactionRepository;

  @InjectMocks
  private BudgetServiceImpl budgetService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private AppUser buildUser() {
    return AppUser.builder()
                  .id(1L)
                  .name("Rishi")
                  .email("rishi@gmail.com")
                  .build();
  }

  private Category buildCategory() {
    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    category.setCategoryType(CategoryType.EXPENSE);
    category.setCategoryScope(CategoryScope.PERSONAL);
    return category;
  }

  private BudgetRequestDTO buildBudgetRequest() {
    BudgetRequestDTO dto = new BudgetRequestDTO();
    dto.setCategoryId(1L);
    dto.setAmount(BigDecimal.valueOf(5000));
    dto.setStartDate(LocalDate.now().plusDays(1));
    dto.setEndDate(LocalDate.now().plusDays(30));
    return dto;
  }

  private Budget buildBudget() {
    Budget budget = new Budget();
    budget.setId(1L);
    budget.setAppUser(buildUser());
    budget.setCategory(buildCategory());
    budget.setAmount(BigDecimal.valueOf(5000));
    budget.setStartDate(LocalDate.now().plusDays(1));
    budget.setEndDate(LocalDate.now().plusDays(30));
    budget.setActive(false);
    budget.setStatus(BudgetStatus.UPCOMING);
    return budget;
  }

  // ================= READ =================

  @Test
  void getAllBudgets_shouldReturnBudgetList() {
    AppUser user = buildUser();
    Budget budget = buildBudget();

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(budgetRepository.findByAppUserId(1L))
      .thenReturn(List.of(budget));

    when(transactionRepository
           .sumAmountByUserIdAndCategoryIdAndDateBetween(
             anyLong(), anyLong(), any(), any()))
      .thenReturn(Optional.of(BigDecimal.ZERO));

    List<BudgetResponseDTO> result =
      budgetService.getAllBudgets(1L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCategoryName()).isEqualTo("Food");
  }

  // ================= CREATE =================

  @Test
  void createBudget_shouldCreateSuccessfully() {
    AppUser user = buildUser();
    Category category = buildCategory();
    BudgetRequestDTO dto = buildBudgetRequest();

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(budgetRepository.existsOverlappingBudgetForNew(
      anyLong(), anyLong(), any(), any()))
      .thenReturn(false);

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(transactionRepository
           .sumAmountByUserIdAndCategoryIdAndDateBetween(
             anyLong(), anyLong(), any(), any()))
      .thenReturn(Optional.of(BigDecimal.ZERO));

    BudgetResponseDTO response =
      budgetService.createBudget(1L, dto);

    assertThat(response).isNotNull();
    assertThat(response.getCategoryName()).isEqualTo("Food");

    verify(budgetRepository).save(any(Budget.class));
  }

  @Test
  void createBudget_shouldThrow_whenOverlapExists() {
    Category category = buildCategory();
    BudgetRequestDTO dto = buildBudgetRequest();

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(budgetRepository.existsOverlappingBudgetForNew(
      anyLong(), anyLong(), any(), any()))
      .thenReturn(true);

    assertThatThrownBy(() ->
                         budgetService.createBudget(1L, dto))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining(
        BudgetResponseMessage.OVERLAPPING_BUDGET_EXISTS.getMessage()
      );
  }

  @Test
  void createBudget_shouldThrow_whenInvalidAmount() {
    BudgetRequestDTO dto = buildBudgetRequest();
    dto.setAmount(BigDecimal.ZERO);

    assertThatThrownBy(() ->
                         budgetService.createBudget(1L, dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining(
        BudgetResponseMessage.INVALID_BUDGET_AMOUNT.getMessage()
      );
  }

  @Test
  void createBudget_shouldThrow_whenInvalidDateRange() {
    BudgetRequestDTO dto = buildBudgetRequest();
    dto.setEndDate(dto.getStartDate().minusDays(1));

    assertThatThrownBy(() ->
                         budgetService.createBudget(1L, dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining(
        BudgetResponseMessage.INVALID_DATE_RANGE.getMessage()
      );
  }

  // ================= UPDATE =================

  @Test
  void updateBudget_shouldUpdateSuccessfully() {
    Budget budget = buildBudget();
    Category category = buildCategory();
    BudgetRequestDTO dto = buildBudgetRequest();

    when(budgetRepository.findById(1L))
      .thenReturn(Optional.of(budget));

    when(budgetRepository.existsOverlappingBudget(
      anyLong(), anyLong(), anyLong(), any(), any()))
      .thenReturn(false);

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(transactionRepository
           .sumAmountByUserIdAndCategoryIdAndDateBetween(
             anyLong(), anyLong(), any(), any()))
      .thenReturn(Optional.of(BigDecimal.ZERO));

    BudgetResponseDTO response =
      budgetService.updateBudget(1L, 1L, dto);

    assertThat(response).isNotNull();

    verify(budgetRepository).save(budget);
  }

  @Test
  void updateBudget_shouldThrow_whenBudgetNotFound() {
    BudgetRequestDTO dto = buildBudgetRequest();

    when(budgetRepository.findById(1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         budgetService.updateBudget(1L, 1L, dto))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(
        BudgetResponseMessage.BUDGET_NOT_FOUND.getMessage()
      );
  }

  // ================= DELETE =================

  @Test
  void deleteBudget_shouldDeleteSuccessfully() {
    Budget budget = buildBudget();

    when(budgetRepository.findById(1L))
      .thenReturn(Optional.of(budget));

    budgetService.deleteBudget(1L, 1L);

    verify(budgetRepository).delete(budget);
  }

  // ================= ANALYTICS =================

  @Test
  void getBudgetAnalytics_shouldReturnAnalyticsList() {
    AppUser user = buildUser();
    Budget budget = buildBudget();

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(budgetRepository.findByAppUserId(1L))
      .thenReturn(List.of(budget));

    when(transactionRepository
           .sumAmountByUserAndCategoryAndDateRange(
             anyLong(), anyLong(), any(), any(), any()))
      .thenReturn(BigDecimal.valueOf(2000));

    List<BudgetAnalyticsDTO> result =
      budgetService.getBudgetAnalytics(1L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getActualSpent())
      .isEqualByComparingTo("2000");
  }
}