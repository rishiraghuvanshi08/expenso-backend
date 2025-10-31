package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.Category;
import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Category} entities.
 * Provides custom queries for both personal and group-based category scopes.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  /**
   * Checks if a personal category with the given name and type exists for a specific user.
   */
  boolean existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(String name, CategoryType type, CategoryScope scope, Long userId);

  /**
   * Checks if a group category with the given name and type exists within a group.
   */
  boolean existsByNameAndCategoryTypeAndCategoryScopeAndExpenseGroupId(String name, CategoryType type, CategoryScope scope, Long groupId);

  /**
   * Retrieves default personal categories shared across all users.
   */
  @Query("SELECT c FROM Category c WHERE c.appUser IS NULL AND c.expenseGroup IS NULL AND c.categoryScope = 'PERSONAL'")
  List<Category> findDefaultPersonalCategories();

  /**
   * Retrieves user-created personal categories for a specific user.
   */
  @Query("SELECT c FROM Category c WHERE c.appUser.id = :userId AND c.categoryScope = 'PERSONAL'")
  List<Category> findUserCreatedPersonalCategories(@Param("userId") Long userId);

  /**
   * Retrieves default group categories shared across all groups.
   */
  @Query("SELECT c FROM Category c WHERE c.appUser IS NULL AND c.expenseGroup IS NULL AND c.categoryScope = 'GROUP'")
  List<Category> findDefaultGroupCategories(); // shared across all groups

  /**
   * Retrieves group-specific categories created by users.
   */
  @Query("SELECT c FROM Category c WHERE c.expenseGroup.id = :groupId AND c.categoryScope = 'GROUP'")
  List<Category> findUserCreatedGroupCategories(@Param("groupId") Long groupId);

  /**
   * Fetches all categories visible to a user across personal and group scopes.
   * Includes default and user-created categories.
   */
  @Query("SELECT c FROM Category c WHERE " +
           "(c.categoryScope = 'PERSONAL' AND (c.appUser IS NULL OR c.appUser.id = :userId)) " +
           "OR (c.categoryScope = 'GROUP' AND c.expenseGroup.id IN :groupIds)")
  List<Category> findVisibleCategories(@Param("userId") Long userId,
                                       @Param("groupIds") List<Long> groupIds);

  /**
   * Merges group-specific and global categories for a given group context.
   */
  @Query("SELECT c FROM Category c WHERE " +
           "(c.categoryScope = 'GROUP' AND (c.expenseGroup.id = :groupId OR (c.expenseGroup IS NULL AND c.appUser IS NULL)))")
  List<Category> findMergedGroupCategories(@Param("groupId") Long groupId);
}
