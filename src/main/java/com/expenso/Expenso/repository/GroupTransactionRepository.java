package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.GroupTransaction;
import com.expenso.Expenso.entities.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link GroupTransaction} entities.
 * Provides methods to perform CRUD operations and custom queries on group transactions.
 */
@Repository
public interface GroupTransactionRepository extends JpaRepository<GroupTransaction, Long> {

  /**
   * Finds transactions by group ID, payer user ID, and category ID.
   */
  List<GroupTransaction> findByExpenseGroupIdAndPaidByUserIdAndCategoryId(Long groupId, Long userId, Long categoryId);

  /**
   * Finds transactions by payer user ID and category ID.
   */
  List<GroupTransaction> findByPaidByUserIdAndCategoryId(Long userId, Long categoryId);
}
