package com.expenso.Expenso.entities;

import com.expenso.Expenso.enums.entity.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_transaction_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser appUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "wallet_id", nullable = true)
  private Wallet wallet;

  @Column(nullable = false, precision = 10, scale = 2)
  @NotNull
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, length = 20)
  @NotNull
  private TransactionType transactionType;

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(nullable = false)
  @NotNull
  private LocalDate date;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

}
