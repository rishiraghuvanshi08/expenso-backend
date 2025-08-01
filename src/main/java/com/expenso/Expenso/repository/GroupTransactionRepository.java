package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.GroupTransaction;
import com.expenso.Expenso.entities.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupTransactionRepository extends JpaRepository<GroupTransaction, Long> {

  List<GroupTransaction> findByExpenseGroupIdAndPaidByUserIdAndCategoryId(Long groupId, Long userId, Long categoryId);

  List<GroupTransaction> findByPaidByUserIdAndCategoryId(Long userId, Long categoryId);
}
