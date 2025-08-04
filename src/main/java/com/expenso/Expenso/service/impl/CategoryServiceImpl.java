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


  @Override
  public List<CategoryResponseDTO> getDefaultPersonalCategories() {
    return categoryRepository.findDefaultPersonalCategories()
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  @Override
  public List<CategoryResponseDTO> getUserCreatedPersonalCategories(Long userId) {
    return categoryRepository.findUserCreatedPersonalCategories(userId)
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  @Override
  public List<CategoryResponseDTO> getDefaultGroupCategories() {
    return categoryRepository.findDefaultGroupCategories()
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

  @Override
  public List<CategoryResponseDTO> getUserCreatedGroupCategories(Long groupId) {
    return categoryRepository.findUserCreatedGroupCategories(groupId)
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

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

  @Override
  public List<CategoryResponseDTO> getMergedGroupCategories(Long groupId) {
    return categoryRepository.findMergedGroupCategories(groupId)
                             .stream().map(this::mapToDTO)
                             .collect(Collectors.toList());
  }

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

  @Override
  public void deleteCategory(Long categoryId, Long userId) {
    Category category = findCategoryOrThrow(categoryId);
    validateDeletable(category, userId);
    categoryRepository.delete(category);
  }

  private void handlePersonalCategoryAccess(Category category, Long userId) {
    AppUser owner = category.getAppUser();
    if (owner != null && !owner.getId().equals(userId)) {
      throwAccessDeniedException();
    }
  }

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

  private void throwAccessDeniedException(){
    throw new AccessDeniedException(CategoryResponseMessage.CATEGORY_VISIBILITY_RESTRICTED.getMessage());
  }

  private void validateDuplicateCategory(boolean isDuplicate){
    if (isDuplicate) {
      throw new ResourceAlreadyExistsException(CategoryResponseMessage.CATEGORY_ALREADY_EXISTS.getMessage());
    }
  }

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

  private void validateUpdatePermissions(Category category, Long userId) {
    if (category.getAppUser() == null) {
      throw new UpdationFailedException(CategoryResponseMessage.CATEGORY_UPDATE_RESTRICTED.getMessage());
    }

    if (!category.getAppUser().getId().equals(userId)) {
      throw new UpdationFailedException(CategoryResponseMessage.CATEGORY_UPDATE_RESTRICTED.getMessage());
    }
  }

  private void validateCategoryTypeChange(Category category, UpdateCategoryRequestDTO dto) {
    boolean isUsed = userTransactionRepository.existsByCategoryIdAndIsDeletedFalse(category.getId());
    if (isUsed && category.getCategoryType() != dto.getCategoryType()) {
      throw new UpdationFailedException(CategoryResponseMessage.CATEGORY_TYPE_UPDATE_RESTRICTED.getMessage());
    }
  }

  private AppUser findUserOrThrow(Long id) {
    return appUserRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(AppUserResponseMessage.USER_NOT_FOUND.getMessage()));
  }

  private Category findCategoryOrThrow(Long categoryId){
    return categoryRepository.findById(categoryId)
                             .orElseThrow(() -> new ResourceNotFoundException((CategoryResponseMessage.CATEGORY_NOT_FOUND.getMessage())));
  }

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

  private CategoryBudgetInfoDTO mapToBudgetDTO(Budget budget) {
    return new CategoryBudgetInfoDTO(
      budget.getId(),
      budget.getAmount(),
      budget.getStartDate(),
      budget.getEndDate(),
      budget.isActive()
    );
  }

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
