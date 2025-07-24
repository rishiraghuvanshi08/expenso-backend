package com.expenso.Expenso.entities;

import com.expenso.Expenso.enums.entity.GroupTransactionSplitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "group_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_transaction_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "expense_group_id", nullable = false)
  private ExpenseGroup expenseGroup;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "paid_by_user_id", nullable = false)
  private AppUser paidByUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "split_type", nullable = false, length = 20)
  private GroupTransactionSplitType groupTransactionSplitType;

  @Column(name = "is_settled", nullable = false)
  private boolean settled;

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(nullable = false)
  private LocalDate date;

  @OneToMany(mappedBy = "groupTransaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TransactionSplit> transactionsSplits;

}