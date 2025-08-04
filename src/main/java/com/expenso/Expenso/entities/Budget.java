package com.expenso.Expenso.entities;

import com.expenso.Expenso.enums.entity.BudgetStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "budget_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser appUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(nullable = false, precision = 10, scale = 2)
  @NotNull
  private BigDecimal amount;

  @Column(name = "start_date", nullable = false)
  @NotNull
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  @NotNull
  private LocalDate endDate;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private BudgetStatus status;
}
