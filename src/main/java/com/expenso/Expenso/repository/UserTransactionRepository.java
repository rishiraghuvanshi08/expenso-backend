package com.expenso.Expenso.repository;

import com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO;
import com.expenso.Expenso.entities.UserTransaction;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, Long> {

  List<UserTransaction> findByAppUserIdAndIsActiveTrue(Long userId);

  Optional<UserTransaction> findByIdAndAppUserIdAndIsActiveTrue(Long id, Long userId);

  @Query("SELECT new com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO(:groupBy, " +
           "CASE WHEN :groupBy = 'CATEGORY' THEN c.name " +
           "     WHEN :groupBy = 'WALLET' THEN w.name " +
           "     WHEN :groupBy = 'DATE' THEN FUNCTION('DATE_FORMAT', t.date, '%Y-%m-%d') " +
           "END, SUM(t.amount)) " +
           "FROM UserTransaction t " +
           "JOIN t.category c " +
           "JOIN t.wallet w " +
           "WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY " +
           "CASE WHEN :groupBy = 'CATEGORY' THEN c.name " +
           "     WHEN :groupBy = 'WALLET' THEN w.name " +
           "     WHEN :groupBy = 'DATE' THEN FUNCTION('DATE_FORMAT', t.date, '%Y-%m-%d') " +
           "END")
  List<TransactionSummaryDTO> getSummaryByGroup(@Param("userId") Long userId, @Param("groupBy") String groupBy);

  @Query("SELECT FUNCTION('MONTH', t.date) AS month, " +
           "SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount ELSE 0 END), " +
           "SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.amount ELSE 0 END) " +
           "FROM UserTransaction t WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY FUNCTION('MONTH', t.date)")
  List<Object[]> getMonthlyStats(@Param("userId") Long userId);
}
