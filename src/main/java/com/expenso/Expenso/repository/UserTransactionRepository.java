package com.expenso.Expenso.repository;

import com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Category;
import com.expenso.Expenso.entities.UserTransaction;
import com.expenso.Expenso.enums.entity.TransactionType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, Long> {

  List<UserTransaction> findByAppUserIdAndIsDeletedFalse(Long userId);

  Optional<UserTransaction> findByIdAndAppUserIdAndIsDeletedFalse(Long transactionId, Long userId);

  @Query("SELECT new com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO(" +
           "com.expenso.Expenso.enums.request.TransactionGroupBy.CATEGORY, " +
           "c.name, " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.INCOME THEN t.amount ELSE 0 END), " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.EXPENSE THEN t.amount ELSE 0 END)) " +
           "FROM UserTransaction t JOIN t.category c " +
           "WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY c.name")
  List<TransactionSummaryDTO> getSummaryByCategory(@Param("userId") Long userId);

  @Query("SELECT new com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO(" +
           "com.expenso.Expenso.enums.request.TransactionGroupBy.WALLET, " +
           "w.name, " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.INCOME THEN t.amount ELSE 0 END), " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.EXPENSE THEN t.amount ELSE 0 END)) " +
           "FROM UserTransaction t JOIN t.wallet w " +
           "WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY w.name")
  List<TransactionSummaryDTO> getSummaryByWallet(@Param("userId") Long userId);

  @Query("SELECT new com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO(" +
           "com.expenso.Expenso.enums.request.TransactionGroupBy.DATE, " +
           "CAST(t.date AS string), " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.INCOME THEN t.amount ELSE 0 END), " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.EXPENSE THEN t.amount ELSE 0 END)) " +
           "FROM UserTransaction t " +
           "WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY t.date ORDER BY t.date")
  List<TransactionSummaryDTO> getSummaryByDate(@Param("userId") Long userId);

  @Query("SELECT new com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO(" +
           "com.expenso.Expenso.enums.request.TransactionGroupBy.MONTH, " +
           "FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date), " +  // pass year and month separately
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.INCOME THEN t.amount ELSE 0 END), " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.EXPENSE THEN t.amount ELSE 0 END)) " +
           "FROM UserTransaction t " +
           "WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date) " +
           "ORDER BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date)")
  List<TransactionSummaryDTO> getSummaryByMonth(@Param("userId") Long userId);

  @Query("SELECT new com.expenso.Expenso.dto.usertransaction.TransactionSummaryDTO(" +
           "com.expenso.Expenso.enums.request.TransactionGroupBy.YEAR, " +
           "FUNCTION('YEAR', t.date), " + // No CAST here
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.INCOME THEN t.amount ELSE 0 END), " +
           "SUM(CASE WHEN t.transactionType = com.expenso.Expenso.enums.entity.TransactionType.EXPENSE THEN t.amount ELSE 0 END)) " +
           "FROM UserTransaction t " +
           "WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY FUNCTION('YEAR', t.date) " +
           "ORDER BY FUNCTION('YEAR', t.date)")
  List<TransactionSummaryDTO> getSummaryByYear(@Param("userId") Long userId);

  @Query("SELECT FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date), " +
           "SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount ELSE 0.0 END), " +
           "SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.amount ELSE 0.0 END) " +
           "FROM UserTransaction t " +
           "WHERE t.appUser.id = :userId AND t.isDeleted = false " +
           "GROUP BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date) " +
           "ORDER BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date)")
  List<Object[]> getMonthlyStats(@Param("userId") Long userId);

  boolean existsByCategoryIdAndIsDeletedFalse(Long categoryId);

  boolean existsByCategoryId(Long categoryId);

  List<UserTransaction> findByAppUserIdAndCategoryIdAndIsDeletedFalse(Long userId, Long categoryId);

  @Query("""
      SELECT ut FROM UserTransaction ut
      WHERE ut.wallet.id = :walletId
      AND ut.wallet.status = 'ACTIVE'
      AND ut.wallet.appUser.id = :userId
      AND ut.wallet.appUser.isActive = true
      ORDER BY ut.date DESC
  """)
  List<UserTransaction> findActiveTransactionsByWalletAndUser(@Param("walletId") Long walletId, @Param("userId") Long userId);

  @Query("SELECT SUM(t.amount) FROM UserTransaction t WHERE t.appUser.id = :userId AND t.category.id = :categoryId AND t.date BETWEEN :start AND :end")
  Optional<BigDecimal> sumAmountByUserIdAndCategoryIdAndDateBetween(@Param("userId") Long userId,
                                                                    @Param("categoryId") Long categoryId,
                                                                    @Param("start") LocalDate start,
                                                                    @Param("end") LocalDate end
  );

  @Query("""
      SELECT SUM(t.amount)
      FROM UserTransaction t
      WHERE t.appUser.id = :userId
        AND t.category.id = :categoryId
        AND t.date BETWEEN :start AND :end
        AND t.transactionType = :type
        AND t.isDeleted = false
  """)
  BigDecimal sumAmountByUserAndCategoryAndDateRange(@Param("user") Long userId,
                                                    @Param("category") Long categoryId,
                                                    @Param("start") LocalDate start,
                                                    @Param("end") LocalDate end,
                                                    @Param("type") TransactionType type);
}
