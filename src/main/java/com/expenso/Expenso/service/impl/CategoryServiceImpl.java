package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.dto.category.CategoryBudgetInfoDTO;
import com.expenso.Expenso.dto.category.CategoryDetailResponseDTO;
import com.expenso.Expenso.dto.category.CategoryResponseDTO;
import com.expenso.Expenso.dto.category.CreateCategoryRequestDTO;
import com.expenso.Expenso.dto.category.UpdateCategoryRequestDTO;
import com.expenso.Expenso.dto.grouptransaction.GroupTransactionDTO;
import com.expenso.Expenso.dto.usertransaction.UserTransactionResponseDTO;
import com.expenso.Expenso.entities.*;
import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.response.AppUserResponseMessage;
import com.expenso.Expenso.enums.response.CategoryResponseMessage;
import com.expenso.Expenso.enums.response.ExpenseGroupResponseMessage;
import com.expenso.Expenso.exception.custom.*;
import com.expenso.Expenso.repository.*;
import com.expenso.Expenso.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CategoryService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final AppUserRepository appUserRepository;
  private final ExpenseGroupRepository expenseGroupRepository;
  private final UserTransactionRepository userTransactionRepository;
  private final GroupTransactionRepository groupTransactionRepository;
  private final BudgetRepository budgetRepository;

  /**
   * @see CategoryService#getCategoryDetails(Long, Long)
   */
  @Override
  public CategoryDetailResponseDTO getCategoryDetails(Long categoryId, Long userId) {
    Category category = findCategoryOrThrow(categoryId);
    CategoryScope scope = category.getCategoryScope();

    List<UserTransaction> userTransactions = Collections.emptyList();
    List<GroupTransaction> groupTransactions = Collections.emptyList();
    List<Budget> budgets = Collections.emptyList();

    switch (scope) {
      case PERSONAL -> {
        handlePersonalCategoryAccess(category, userId);
        userTransactions = userTransactionRepository
                             .findByAppUserIdAndCategoryIdAndIsDeletedFalse(userId, categoryId);
        budgets = budgetRepository.findByAppUserIdAndCategoryId(userId, categoryId);
      }
      case GROUP -> {
        groupTransactions = handleGroupCategoryAccessAndFetch(category, userId, categoryId);
      }
      default -> throw new IllegalStateException("Unknown category scope: " + scope);
    }

    return mapToCategoryDetailDTO(category, userTransactions, groupTransactions, budgets);
  }

  /**
   * @see CategoryService#getDefaultPersonalCategories()
   */
  @Override
  public List<CategoryResponseDTO> getDefaultPersonalCategories() {
    return categoryRepository.findDefaultPersonalCategories()
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  /**
   * @see CategoryService#getUserCreatedPersonalCategories(Long) 
   */
  @Override
  public List<CategoryResponseDTO> getUserCreatedPersonalCategories(Long userId) {
    return categoryRepository.findUserCreatedPersonalCategories(userId)
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  /**
   * @see CategoryService#getDefaultGroupCategories() 
   */
  @Override
  public List<CategoryResponseDTO> getDefaultGroupCategories() {
    return categoryRepository.findDefaultGroupCategories()
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  /**
   * @see CategoryService#getUserCreatedGroupCategories(Long) 
   */
  @Override
  public List<CategoryResponseDTO> getUserCreatedGroupCategories(Long groupId) {
    return categoryRepository.findUserCreatedGroupCategories(groupId)
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  /**
   * @see CategoryService#getAllVisibleCategories(Long) 
   */
  @Override
  public List<CategoryResponseDTO> getAllVisibleCategories(Long userId) {
    AppUser user = findUserOrThrow(userId);
    List<Long> groupIds = user.getExpenseGroups().stream()
                              .map(ExpenseGroup::getId)
                              .collect(Collectors.toList());

    return categoryRepository.findVisibleCategories(userId, groupIds)
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  /**
   * @see CategoryService#getMergedGroupCategories(Long) 
   */
  @Override
  public List<CategoryResponseDTO> getMergedGroupCategories(Long groupId) {
    return categoryRepository.findMergedGroupCategories(groupId)
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  /**
   * @see CategoryService#createPersonalCategory(Long, CreateCategoryRequestDTO) 
   */
  @Override
  public CategoryResponseDTO createPersonalCategory(Long userId, CreateCategoryRequestDTO dto) {
    AppUser user = findUserOrThrow(userId);

    validateDuplicateCategory(categoryRepository.existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(
      dto.getName(), dto.getCategoryType(), CategoryScope.PERSONAL, userId
    ));

    Category category = new Category();
    category.setName(dto.getName());
    category.setCategoryType(dto.getCategoryType());
    category.setCategoryScope(CategoryScope.PERSONAL);
    category.setAppUser(user);  // Personal

    return mapToDTO(categoryRepository.save(category));
  }

  /**
   * @see CategoryService#createGroupCategory(Long, Long, CreateCategoryRequestDTO) 
   */
  @Override
  public CategoryResponseDTO createGroupCategory(Long groupId, Long userId, CreateCategoryRequestDTO dto) {
    AppUser user = findUserOrThrow(userId);

    ExpenseGroup group = expenseGroupRepository.findById(groupId)
                                               .orElseThrow(() -> new ResourceNotFoundException(ExpenseGroupResponseMessage.GROUP_NOT_FOUND.getMessage()));

    validateDuplicateCategory(categoryRepository.existsByNameAndCategoryTypeAndCategoryScopeAndExpenseGroupId(
      dto.getName(), dto.getCategoryType(), CategoryScope.GROUP, groupId
    ));

    Category category = new Category();
    category.setName(dto.getName());
    category.setCategoryType(dto.getCategoryType());
    category.setCategoryScope(CategoryScope.GROUP);
    category.setExpenseGroup(group);
    category.setAppUser(user);

    return mapToDTO(categoryRepository.save(category));
  }

  /**
   * @see CategoryService#updateCategory(Long, Long, UpdateCategoryRequestDTO) 
   */
  @Override
  public CategoryResponseDTO updateCategory(Long categoryId, Long userId, UpdateCategoryRequestDTO dto) {
    Category category = findCategoryOrThrow(categoryId);

    validateUpdatePermissions(category, userId);
    validateCategoryTypeChange(category, dto);

    boolean isNameChanged = !category.getName().equalsIgnoreCase(dto.getName());
    boolean isTypeChanged = category.getCategoryType() != dto.getCategoryType();

    if (isNameChanged || isTypeChanged) {
      boolean isDuplicate;

      if (category.getCategoryScope() == CategoryScope.PERSONAL) {
        isDuplicate = categoryRepository.existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(
          dto.getName(), dto.getCategoryType(), CategoryScope.PERSONAL, userId);
      } else {
        Long groupId = category.getExpenseGroup().getId();
        isDuplicate = categoryRepository.existsByNameAndCategoryTypeAndCategoryScopeAndExpenseGroupId(
          dto.getName(), dto.getCategoryType(), CategoryScope.GROUP, groupId);
      }
      validateDuplicateCategory(isDuplicate);
    }

    category.setName(dto.getName());
    category.setCategoryType(dto.getCategoryType());

    return mapToDTO(categoryRepository.save(category));
  }

  /**
   * @see CategoryService#deleteCategory(Long, Long) 
   */
  @Override
  public void deleteCategory(Long categoryId, Long userId) {
    Category category = findCategoryOrThrow(categoryId);
    validateDeletable(category, userId);
    categoryRepository.delete(category);
  }

  /**
   * Validates access permissions for a personal category
   *
   * @param category The personal category being accessed.
   * @param userId   ID of the requesting user.
   * @throws AccessDeniedException
   */
  private void handlePersonalCategoryAccess(Category category, Long userId) {
    AppUser owner = category.getAppUser();
    if (owner != null && !owner.getId().equals(userId)) {
      throwAccessDeniedException();
    }
  }

  /**
   * Handles access validation for group-scoped categories and fetches
   * associated group transactions for the requesting user.
   *
   * @param category   The group category being accessed.
   * @param userId     ID of the requesting user.
   * @param categoryId ID of the category.
   * @return List of group transactions associated with the user and category.
   * @throws AccessDeniedException if the user is not a member of the group.
   */
  private List<GroupTransaction> handleGroupCategoryAccessAndFetch(Category category, Long userId, Long categoryId) {
    ExpenseGroup group = category.getExpenseGroup();

    if (group == null) {
      // Default group category: show only if user used it
      return groupTransactionRepository.findByPaidByUserIdAndCategoryId(userId, categoryId);
    }

    boolean isGroupMember = group.getGroupMembers().stream()
                                 .anyMatch(member -> member.getMember().getId().equals(userId));
    if (!isGroupMember) {
      throwAccessDeniedException();
    }

    return groupTransactionRepository.findByExpenseGroupIdAndPaidByUserIdAndCategoryId(group.getId(), userId, categoryId);
  }

  /**
   * Throws a standardized access denied exception used across category operations.
   *
   * @throws AccessDeniedException with a predefined category visibility message.
   */
  private void throwAccessDeniedException(){
    throw new AccessDeniedException(CategoryResponseMessage.CATEGORY_VISIBILITY_RESTRICTED.getMessage());
  }

  /**
   * Validates that a category being created or updated does not already exist
   * with the same name, type, and scope (personal or group).
   *
   * @param isDuplicate True if a duplicate category already exists.
   * @throws ResourceAlreadyExistsException if category duplication is detected.
   */
  private void validateDuplicateCategory(boolean isDuplicate){
    if (isDuplicate) {
      throw new ResourceAlreadyExistsException(CategoryResponseMessage.CATEGORY_ALREADY_EXISTS.getMessage());
    }
  }

  /**
   * Validates if a category can be safely deleted based on ownership,
   * system restrictions, and transaction usage.
   * <p>
   * Blocks deletion of:
   * <ul>
   *   <li>System-defined default categories</li>
   *   <li>Categories owned by another user</li>
   *   <li>Categories already used in transactions</li>
   * </ul>
   *
   * @param category The category to be deleted.
   * @param userId   ID of the user performing the deletion.
   * @throws DeletionFailedException if deletion is restricted or category is in use.
   */
  private void validateDeletable(Category category, Long userId) {
    // Block deletion of default system categories
    if (category.getAppUser() == null && category.getExpenseGroup() == null) {
      throw new DeletionFailedException(CategoryResponseMessage.CATEGORY_DELETION_RESTRICTED.getMessage());
    }
    // Ensure only creator can delete personal/group categories
    if (category.getAppUser() != null && !category.getAppUser().getId().equals(userId)) {
      throw new DeletionFailedException(CategoryResponseMessage.CATEGORY_DELETION_RESTRICTED.getMessage());
    }
    // Check if the category is used in any transaction (even soft deleted)
    boolean usedInAnyTransactions = userTransactionRepository.existsByCategoryId(category.getId());
    if (usedInAnyTransactions) {
      throw new DeletionFailedException(CategoryResponseMessage.CATEGORY_USAGE_CONSTRAINT.getMessage());
    }
  }

  /**
   * Validates whether the requesting user is allowed to update the given category
   *
   * @param category The category being updated.
   * @param userId   ID of the user attempting the update.
   * @throws UpdationFailedException if the user is not the owner or category is not user-specific.
   */
  private void validateUpdatePermissions(Category category, Long userId) {
    if (category.getAppUser() == null) {
      throw new UpdationFailedException(CategoryResponseMessage.CATEGORY_UPDATE_RESTRICTED.getMessage());
    }

    if (!category.getAppUser().getId().equals(userId)) {
      throw new UpdationFailedException(CategoryResponseMessage.CATEGORY_UPDATE_RESTRICTED.getMessage());
    }
  }

  /**
   * Ensures that a category's type (EXPENSE or INCOME) cannot be changed
   * if it has already been used in any active transactions.
   *
   * @param category The existing category.
   * @param dto      DTO containing updated category type.
   * @throws UpdationFailedException if the category type change violates constraints.
   */
  private void validateCategoryTypeChange(Category category, UpdateCategoryRequestDTO dto) {
    boolean isUsed = userTransactionRepository.existsByCategoryIdAndIsDeletedFalse(category.getId());
    if (isUsed && category.getCategoryType() != dto.getCategoryType()) {
      throw new UpdationFailedException(CategoryResponseMessage.CATEGORY_TYPE_UPDATE_RESTRICTED.getMessage());
    }
  }

  /**
   * Fetches a user entity by ID, throwing a {@link ResourceNotFoundException}
   * if the user does not exist.
   *
   * @param id User ID to fetch.
   * @return The {@link AppUser} entity.
   * @throws ResourceNotFoundException if the user is not found.
   */
  private AppUser findUserOrThrow(Long id) {
    return appUserRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(AppUserResponseMessage.USER_NOT_FOUND.getMessage()));
  }

  /**
   * Fetches a category entity by ID, throwing a {@link ResourceNotFoundException}
   * if the category does not exist.
   *
   * @param categoryId ID of the category to fetch.
   * @return The {@link Category} entity.
   * @throws ResourceNotFoundException if the category is not found.
   */
  private Category findCategoryOrThrow(Long categoryId){
    return categoryRepository.findById(categoryId)
                             .orElseThrow(() -> new ResourceNotFoundException((CategoryResponseMessage.CATEGORY_NOT_FOUND.getMessage())));
  }

  /**
   * Converts a {@link Category} entity to a simplified {@link CategoryResponseDTO}
   * for use in API responses.
   *
   * @param category The category entity to convert.
   * @return DTO representation of the category.
   */
  private CategoryResponseDTO mapToDTO(Category category) {
    return CategoryResponseDTO.builder()
                              .id(category.getId())
                              .name(category.getName())
                              .categoryType(category.getCategoryType())
                              .categoryScope(category.getCategoryScope())
                              .userId(category.getAppUser() != null ? category.getAppUser().getId() : null)
                              .groupId(category.getExpenseGroup() != null ? category.getExpenseGroup().getId() : null)
                              .build();
  }

  /**
   * Builds a comprehensive {@link CategoryDetailResponseDTO} object containing
   * category metadata and its related transactions and budgets.
   *
   * @param category          The category entity.
   * @param userTransactions  List of personal transactions linked to the category.
   * @param groupTransactions List of group transactions linked to the category.
   * @param budgets           List of budgets assigned to the category.
   * @return A fully populated category detail DTO.
   */
  private CategoryDetailResponseDTO mapToCategoryDetailDTO(Category category,
                                                           List<UserTransaction> userTransactions,
                                                           List<GroupTransaction> groupTransactions,
                                                           List<Budget> budgets) {
    return new CategoryDetailResponseDTO(
      category.getId(),
      category.getName(),
      category.getCategoryType(),
      category.getCategoryScope(),
      userTransactions.stream().map(this::mapToUserTransactionDTO).collect(Collectors.toList()),
      groupTransactions.stream().map(this::mapToGroupTransactionDTO).collect(Collectors.toList()),
      budgets.stream().map(this::mapToBudgetDTO).collect(Collectors.toList())
    );
  }

  /**
   * Converts a {@link UserTransaction} entity into a lightweight DTO
   * representation for use in category detail responses.
   *
   * @param tx The user transaction entity.
   * @return {@link UserTransactionResponseDTO} containing transaction info.
   */
  private UserTransactionResponseDTO mapToUserTransactionDTO(UserTransaction tx) {
    return UserTransactionResponseDTO.builder()
                                     .id(tx.getId())
                                     .categoryName(tx.getCategory().getName())
                                     .walletName(
                                       tx.getWallet() != null ? tx.getWallet().getName() : null
                                     )
                                     .amount(tx.getAmount())
                                     .transactionType(tx.getTransactionType())
                                     .note(tx.getNote())
                                     .date(tx.getDate())
                                     .build();
  }

  /**
   * Converts a {@link Budget} entity into a {@link CategoryBudgetInfoDTO},
   * used to represent budget details linked to a category.
   *
   * @param budget The budget entity.
   * @return DTO containing budget summary information.
   */
  private CategoryBudgetInfoDTO mapToBudgetDTO(Budget budget) {
    return new CategoryBudgetInfoDTO(
      budget.getId(),
      budget.getAmount(),
      budget.getStartDate(),
      budget.getEndDate(),
      budget.isActive()
    );
  }

  /**
   * Converts a {@link GroupTransaction} entity into a {@link GroupTransactionDTO}
   * for inclusion in category detail responses.
   *
   * @param transaction The group transaction entity.
   * @return DTO containing summarized transaction details.
   */
  private GroupTransactionDTO mapToGroupTransactionDTO(GroupTransaction transaction) {
    return new GroupTransactionDTO(
      transaction.getId(),
      transaction.getAmount(),
      transaction.getNote(),
      transaction.isSettled(),
      transaction.getDate(),
      transaction.getGroupTransactionSplitType(),
      transaction.getExpenseGroup().getId(),
      transaction.getExpenseGroup().getName(),
      transaction.getPaidByUser().getId(),
      transaction.getPaidByUser().getName()
    );
  }
}
