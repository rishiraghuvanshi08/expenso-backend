package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link ExpenseGroup} entities.
 * Provides methods to perform CRUD operations and custom queries related to expense groups.
 */
@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {
}
