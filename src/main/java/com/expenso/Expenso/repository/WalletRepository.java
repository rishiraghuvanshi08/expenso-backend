package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.UserTransaction;
import com.expenso.Expenso.entities.Wallet;
import com.expenso.Expenso.enums.entity.WalletStatus;
import com.expenso.Expenso.enums.entity.WalletType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Wallet} entities.
 * Provides methods to perform CRUD operations and custom queries on user wallets.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

  /**
   * Finds all wallets for a user by status.
   */
  List<Wallet> findAllByAppUserIdAndStatus(Long userId, WalletStatus status);

  /**
   * Finds an active wallet for a specific active user.
   */
  @Query("""
      SELECT w FROM Wallet w
      WHERE w.id = :walletId
      AND w.appUser.id = :userId
      AND w.status = 'ACTIVE'
      AND w.appUser.isActive = true
  """)
  Optional<Wallet> findActiveWalletForActiveUser(@Param("walletId") Long walletId, @Param("userId") Long userId);

  /**
   * Checks if an active wallet with the same name and type exists for a user, excluding a given wallet ID.
   */
  @Query("""
      SELECT COUNT(w) > 0 FROM Wallet w
      WHERE w.appUser.id = :userId
      AND LOWER(w.name) = LOWER(:name)
      AND w.walletType = :walletType
      AND w.status = 'ACTIVE'
      AND (:excludeWalletId IS NULL OR w.id <> :excludeWalletId)
  """)
  boolean existsActiveWalletByNameAndTypeExcludingId(@Param("userId") Long userId,
                                                     @Param("name") String name,
                                                     @Param("walletType") WalletType walletType,
                                                     @Param("excludeWalletId") Long excludeWalletId);

  /**
   * Retrieves the total balance across all active wallets for a user.
   */
  @Query("SELECT COALESCE(SUM(w.balance), 0) FROM Wallet w WHERE w.appUser.id = :userId AND w.status = 'ACTIVE'")
  BigDecimal findTotalBalanceByUserId(@Param("userId") Long userId);
}
