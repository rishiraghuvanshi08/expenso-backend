package com.expenso.Expenso.entities;

import com.expenso.Expenso.entities.enums.GroupTransactionSplitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction_split")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSplit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_split_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "group_transaction_id", nullable = false)
  private GroupTransaction groupTransaction;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser appUser;

  @Column(name = "split_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal splitAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "share_type", nullable = false, length = 20)
  private GroupTransactionSplitType groupTransactionSplitType;

  @Column(name = "percentage_share")
  private BigDecimal percentageShare;

  @Column(name = "share_count")
  private Integer shareCount;

  @Column(name = "note")
  private String note;

  @Column(name = "is_paid_back", nullable = false)
  private boolean isPaidBack;

}
