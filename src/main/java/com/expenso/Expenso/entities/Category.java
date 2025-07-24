package com.expenso.Expenso.entities;

import com.expenso.Expenso.enums.entity.CategoryScope;
import com.expenso.Expenso.enums.entity.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Long id;

  @Column(name = "category_name", nullable = false, length = 100)
  @NotBlank
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "category_type", nullable = false, length = 20)
  @NotNull
  private CategoryType categoryType;

  @Enumerated(EnumType.STRING)
  @Column(name = "category_scope", nullable = false, length = 20)
  @NotNull
  private CategoryScope categoryScope;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private AppUser appUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "expense_group_id")
  private ExpenseGroup expenseGroup;

  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Budget> budgets;

  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<UserTransaction> userTransactions;

  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<GroupTransaction> groupTransactions;

}
