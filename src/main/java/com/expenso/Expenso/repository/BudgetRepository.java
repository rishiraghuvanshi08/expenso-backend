package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Budget} entities.
 * Provides methods to perform CRUD operations, custom lookups,
 * and overlap validations for user budgets.
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

  /**
   * Finds all budgets with status ACTIVE or UPCOMING.
   */
  @Query("SELECT b FROM Budget b WHERE b.status IN ('ACTIVE', 'UPCOMING')")
  List<Budget> findAllForStatusUpdate();

  /**
   * Finds budgets by user and category.
   */
  List<Budget> findByAppUserIdAndCategoryId(Long userId, Long categoryId);

  /**
   * Finds all budgets for a user.
   */
  List<Budget> findByAppUserId(Long userId);

  /**
   * Checks if an overlapping budget exists (excluding a specific budget).
   */
  @Query("SELECT COUNT(b) > 0 FROM Budget b " +
           "WHERE b.appUser.id = :userId AND b.category.id = :categoryId " +
           "AND b.id <> :excludeBudgetId " +
           "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
  boolean existsOverlappingBudget(Long userId, Long categoryId, Long excludeBudgetId, LocalDate startDate, LocalDate endDate);

  /**
   * Checks if an overlapping budget exists for a new budget.
   */
  @Query("SELECT COUNT(b) > 0 FROM Budget b " +
           "WHERE b.appUser.id = :userId AND b.category.id = :categoryId " +
           "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
  boolean existsOverlappingBudgetForNew(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

  /**
   * Finds a budget by its ID and user ID.
   */
  Optional<Budget> findByIdAndAppUserId(Long budgetId, Long userId);
}
