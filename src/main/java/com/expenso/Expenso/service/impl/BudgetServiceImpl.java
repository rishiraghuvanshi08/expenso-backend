package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.dto.budget.BudgetAnalyticsDTO;
import com.expenso.Expenso.dto.budget.BudgetRequestDTO;
import com.expenso.Expenso.dto.budget.BudgetResponseDTO;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Budget;
import com.expenso.Expenso.entities.Category;
import com.expenso.Expenso.enums.entity.BudgetStatus;
import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import com.expenso.Expenso.enums.entity.TransactionType;
import com.expenso.Expenso.enums.response.AppUserResponseMessage;
import com.expenso.Expenso.enums.response.BudgetResponseMessage;
import com.expenso.Expenso.enums.response.CategoryResponseMessage;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.repository.BudgetRepository;
import com.expenso.Expenso.repository.CategoryRepository;
import com.expenso.Expenso.repository.UserTransactionRepository;
import com.expenso.Expenso.service.BudgetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link BudgetService}
 */
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

  private final BudgetRepository budgetRepository;
  private final AppUserRepository appUserRepository;
  private final CategoryRepository categoryRepository;
  private final UserTransactionRepository transactionRepository;

  /**
   * @see BudgetService#getAllBudgets(Long)
   */
  @Override
  public List<BudgetResponseDTO> getAllBudgets(Long userId) {
    AppUser user = getUser(userId);
    return budgetRepository.findByAppUserId(user.getId()).stream()
                           .map(this::mapToBudgetResponseDTO)
                           .collect(Collectors.toList());
  }

  /**
   * @see BudgetService#createBudget(Long, BudgetRequestDTO)
   */
  @Override
  @Transactional
  public BudgetResponseDTO createBudget(Long userId, BudgetRequestDTO dto) {
    validateDtoDetails(dto);

    Category category = validateCategory(dto, userId);

    boolean overlap = budgetRepository.existsOverlappingBudgetForNew(
      userId, dto.getCategoryId(), dto.getStartDate(), dto.getEndDate());

    if (overlap) {
      throw new IllegalStateException(BudgetResponseMessage.OVERLAPPING_BUDGET_EXISTS.getMessage());
    }

    AppUser user = getUser(userId);

    Budget budget = new Budget();
    budget.setAppUser(user);
    budget.setCategory(category);
    budget.setAmount(dto.getAmount());
    budget.setStartDate(dto.getStartDate());
    budget.setEndDate(dto.getEndDate());
    updateActiveStatus(budget);

    budgetRepository.save(budget);
    return mapToBudgetResponseDTO(budget);
  }

  /**
   * @see BudgetService#updateBudget(Long, Long, BudgetRequestDTO)
   */
  @Override
  @Transactional
  public BudgetResponseDTO updateBudget(Long userId, Long budgetId, BudgetRequestDTO dto) {
    validateDtoDetails(dto);

    Budget budget = getBudget(budgetId, userId);

    Long oldCategoryId = budget.getCategory().getId();
    Long newCategoryId = dto.getCategoryId();

    // If category or dates changed, check for overlapping with other budgets
    if (!oldCategoryId.equals(newCategoryId) ||
          !dto.getStartDate().equals(budget.getStartDate()) ||
          !dto.getEndDate().equals(budget.getEndDate())) {

      boolean overlaps = budgetRepository.existsOverlappingBudget(
        userId, newCategoryId, budgetId, dto.getStartDate(), dto.getEndDate());

      if (overlaps) {
        throw new IllegalStateException(BudgetResponseMessage.OVERLAPPING_BUDGET_ON_UPDATE.getMessage());
      }
    }

    Category category = validateCategory(dto, userId);

    budget.setCategory(category);
    budget.setAmount(dto.getAmount());
    budget.setStartDate(dto.getStartDate());
    budget.setEndDate(dto.getEndDate());
    updateActiveStatus(budget);

    budgetRepository.save(budget);
    return mapToBudgetResponseDTO(budget);
  }

  /**
   * @see BudgetService#deleteBudget(Long, Long)
   */
  @Override
  public void deleteBudget(Long userId, Long budgetId) {
    Budget budget = getBudget(budgetId, userId);
    budgetRepository.delete(budget);
  }

  /**
   * @see BudgetService#getBudgetAnalytics(Long)
   */
  @Override
  public List<BudgetAnalyticsDTO> getBudgetAnalytics(Long userId) {
    AppUser user = getUser(userId);
    return budgetRepository.findByAppUserId(user.getId()).stream()
                           .map(budget -> {
                             BigDecimal spent = transactionRepository.sumAmountByUserAndCategoryAndDateRange(
                               user.getId(), budget.getCategory().getId(), budget.getStartDate(), budget.getEndDate(), TransactionType.EXPENSE);
                             spent = spent != null ? spent : BigDecimal.ZERO;

                             return BudgetAnalyticsDTO.builder()
                                                      .categoryName(budget.getCategory().getName())
                                                      .budgetedAmount(budget.getAmount())
                                                      .actualSpent(spent)
                                                      .remaining(budget.getAmount().subtract(spent))
                                                      .percentageUsed(
                                                        budget.getAmount().compareTo(BigDecimal.ZERO) > 0 ?
                                                          spent.divide(budget.getAmount(), 2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100 : 0
                                                      )
                                                      .startDate(budget.getStartDate())
                                                      .endDate(budget.getEndDate())
                                                      .build();
                           }).collect(Collectors.toList());
  }

  /**
   * Retrieves and validates a budget belonging to a specific user.
   *
   * @param budgetId ID of the budget to retrieve.
   * @param userId   ID of the user who owns the budget.
   * @return The validated {@link Budget} entity.
   * @throws ResourceNotFoundException if the budget does not exist or does not belong to the user.
   */
  private Budget getBudget(Long budgetId, Long userId) {
    return budgetRepository.findById(budgetId)
                           .filter(b -> b.getAppUser().getId().equals(userId))
                           .orElseThrow(() -> new ResourceNotFoundException(BudgetResponseMessage.BUDGET_NOT_FOUND.getMessage()));
  }

  /**
   * Retrieves and validates an existing user by ID.
   *
   * @param userId ID of the user to retrieve.
   * @return The validated {@link AppUser} entity.
   * @throws ResourceNotFoundException if the user does not exist.
   */
  private AppUser getUser(Long userId) {
    return appUserRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException(AppUserResponseMessage.USER_NOT_FOUND.getMessage()));
  }

  /**
   * Retrieves and validates a category by ID.
   *
   * @param categoryId ID of the category to retrieve.
   * @return The validated {@link Category} entity.
   * @throws ResourceNotFoundException if the category does not exist.
   */
  private Category getCategory(Long categoryId) {
    return categoryRepository.findById(categoryId)
                             .orElseThrow(() -> new ResourceNotFoundException(CategoryResponseMessage.CATEGORY_NOT_FOUND.getMessage()));
  }

  /**
   * Validates category details for budget creation or update.
   *
   * @param dto    The {@link BudgetRequestDTO} containing category details.
   * @param userId ID of the user creating/updating the budget.
   * @return The validated {@link Category} entity.
   * @throws IllegalArgumentException if any validation fails.
   */
  private Category validateCategory(BudgetRequestDTO dto, Long userId){
    Category category = getCategory(dto.getCategoryId());
    // Check EXPENSE category
    if (!CategoryType.EXPENSE.name().equalsIgnoreCase(category.getCategoryType().name())) {
      throw new IllegalArgumentException(BudgetResponseMessage.INVALID_CATEGORY_TYPE.getMessage());
    }
    // Check PERSONAL scope
    if (!CategoryScope.PERSONAL.equals(category.getCategoryScope())) {
      throw new IllegalArgumentException(BudgetResponseMessage.INVALID_CATEGORY_SCOPE.getMessage());
    }
    // Check if custom category: must belong to the user
    if (category.getAppUser() != null && !category.getAppUser().getId().equals(userId)) {
      throw new IllegalArgumentException(BudgetResponseMessage.UNAUTHORIZED_CUSTOM_CATEGORY.getMessage());
    }
    return category;
  }

  /**
   * Validates the details of a {@link BudgetRequestDTO} object.
   *
   * @param dto The budget request DTO to validate.
   * @throws IllegalArgumentException if any validation rule fails.
   */
  private void validateDtoDetails(BudgetRequestDTO dto){
    if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(BudgetResponseMessage.INVALID_BUDGET_AMOUNT.getMessage());
    }

    if (dto.getStartDate() == null || dto.getStartDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException(BudgetResponseMessage.INVALID_START_DATE.getMessage());
    }

    if (dto.getEndDate() == null || dto.getEndDate().isBefore(dto.getStartDate())) {
      throw new IllegalArgumentException(BudgetResponseMessage.INVALID_DATE_RANGE.getMessage());
    }
  }

  /**
   * Updates the active status and budget status based on current date
   * relative to the budget's start and end dates.
   *
   * @param budget The {@link Budget} entity to update.
   */
  private void updateActiveStatus(Budget budget) {
    LocalDate today = LocalDate.now();

    if (today.isBefore(budget.getStartDate())) {
      budget.setActive(false);
      budget.setStatus(BudgetStatus.UPCOMING);
    } else if (today.isAfter(budget.getEndDate())) {
      budget.setActive(false);
      budget.setStatus(BudgetStatus.EXPIRED);
    } else {
      budget.setActive(true);
      budget.setStatus(BudgetStatus.ACTIVE);
    }
  }

  /**
   * Determines the current status of a given budget based on today's date.
   *
   * @param budget The {@link Budget} entity.
   * @return The determined {@link BudgetStatus}.
   */
  private BudgetStatus getStatus(Budget budget) {
    LocalDate today = LocalDate.now();
    if (today.isBefore(budget.getStartDate())) return BudgetStatus.UPCOMING;
    else if (today.isAfter(budget.getEndDate())) return BudgetStatus.EXPIRED;
    return BudgetStatus.ACTIVE;
  }

  /**
   * Maps a {@link Budget} entity to a {@link BudgetResponseDTO}, calculating
   * total spent amount and remaining balance for the defined period.
   *
   * @param budget The budget entity to map.
   * @return A populated {@link BudgetResponseDTO} with calculated fields.
   */
  private BudgetResponseDTO mapToBudgetResponseDTO(Budget budget) {
    BigDecimal spent = transactionRepository
                         .sumAmountByUserIdAndCategoryIdAndDateBetween(
                           budget.getAppUser().getId(),
                           budget.getCategory().getId(),
                           budget.getStartDate(),
                           budget.getEndDate())
                         .orElse(BigDecimal.ZERO);

    return new BudgetResponseDTO(
      budget.getId(),
      budget.getCategory().getId(),
      budget.getCategory().getName(),
      budget.getAmount(),
      budget.getStartDate(),
      budget.getEndDate(),
      budget.isActive(),
      getStatus(budget),
      spent
    );
  }
}