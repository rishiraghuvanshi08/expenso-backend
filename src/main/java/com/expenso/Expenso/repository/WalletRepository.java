package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
