package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

  @Query("SELECT b FROM Budget b WHERE b.status IN ('ACTIVE', 'UPCOMING')")
  List<Budget> findAllForStatusUpdate();

  List<Budget> findByAppUserIdAndCategoryId(Long userId, Long categoryId);

  List<Budget> findByAppUserId(Long userId);

  @Query("SELECT COUNT(b) > 0 FROM Budget b " +
           "WHERE b.appUser.id = :userId AND b.category.id = :categoryId " +
           "AND b.id <> :excludeBudgetId " +
           "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
  boolean existsOverlappingBudget(Long userId, Long categoryId, Long excludeBudgetId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT COUNT(b) > 0 FROM Budget b " +
           "WHERE b.appUser.id = :userId AND b.category.id = :categoryId " +
           "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
  boolean existsOverlappingBudgetForNew(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

  Optional<Budget> findByIdAndAppUserId(Long budgetId, Long userId);
}
