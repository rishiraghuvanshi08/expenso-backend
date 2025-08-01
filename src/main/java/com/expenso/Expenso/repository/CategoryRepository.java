package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.Category;
import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsByNameAndCategoryTypeAndCategoryScopeAndAppUserId(String name, CategoryType type, CategoryScope scope, Long userId);

  boolean existsByNameAndCategoryTypeAndCategoryScopeAndExpenseGroupId(String name, CategoryType type, CategoryScope scope, Long groupId);

  // PERSONAL

  @Query("SELECT c FROM Category c WHERE c.appUser IS NULL AND c.expenseGroup IS NULL AND c.categoryScope = 'PERSONAL'")
  List<Category> findDefaultPersonalCategories(); // shared across all users

  @Query("SELECT c FROM Category c WHERE c.appUser.id = :userId AND c.categoryScope = 'PERSONAL'")
  List<Category> findUserCreatedPersonalCategories(@Param("userId") Long userId);


  // GROUP

  @Query("SELECT c FROM Category c WHERE c.appUser IS NULL AND c.expenseGroup IS NULL AND c.categoryScope = 'GROUP'")
  List<Category> findDefaultGroupCategories(); // shared across all groups

  @Query("SELECT c FROM Category c WHERE c.expenseGroup.id = :groupId AND c.categoryScope = 'GROUP'")
  List<Category> findUserCreatedGroupCategories(@Param("groupId") Long groupId);


  // COMBINED FETCH

  @Query("SELECT c FROM Category c WHERE " +
           "(c.categoryScope = 'PERSONAL' AND (c.appUser IS NULL OR c.appUser.id = :userId)) " +
           "OR (c.categoryScope = 'GROUP' AND c.expenseGroup.id IN :groupIds)")
  List<Category> findVisibleCategories(@Param("userId") Long userId,
                                       @Param("groupIds") List<Long> groupIds);

  @Query("SELECT c FROM Category c WHERE " +
           "(c.categoryScope = 'GROUP' AND (c.expenseGroup.id = :groupId OR (c.expenseGroup IS NULL AND c.appUser IS NULL)))")
  List<Category> findMergedGroupCategories(@Param("groupId") Long groupId);
}
