package com.expenso.Expenso.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "expense_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "expense_group_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private AppUser createdGroup;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "expenseGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Category> categories;

  @OneToMany(mappedBy = "expenseGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupMember> groupMembers;

  @OneToMany(mappedBy = "expenseGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupTransaction> groupTransactions;

  @OneToMany(mappedBy = "expenseGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Settlement> settlements;

  @OneToMany(mappedBy = "expenseGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupInvitation> groupInvitations;

  @OneToMany(mappedBy = "expenseGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notification> notifications;

  @OneToMany(mappedBy = "expenseGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Alert> alerts;

}
