package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

  List<Budget> findByAppUserIdAndCategoryId(Long userId, Long categoryId);
}
