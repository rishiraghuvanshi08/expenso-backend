package com.expenso.Expenso.entities;

import com.expenso.Expenso.enums.entity.SettlementMethod;
import com.expenso.Expenso.enums.entity.SettlementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "settlement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "settlement_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payer_user_id", nullable = false)
  private AppUser payerUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "receiver_user_id", nullable = false)
  private AppUser receiverUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "expense_group_id", nullable = false)
  private ExpenseGroup expenseGroup;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "settlement_status", nullable = false, length = 20)
  private SettlementStatus settlementStatus;

  @Column(columnDefinition = "TEXT", nullable = true)
  private String note;

  @Enumerated(EnumType.STRING)
  @Column(name = "settlement_method", nullable = false, length = 20)
  private SettlementMethod settlementMethod;

  @Column(nullable = false)
  private LocalDate date;

}
