package com.expenso.Expenso.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(name = "name", nullable = false)
  @NotBlank
  private String name;

  @Column(name = "email", nullable = false, unique = true)
  @NotNull
  @Email
  private String email;

  @Column(name = "password", nullable = false)
  @NotNull
  @Size(min = 8)
  private String password;

  @Column(name = "phone_number", nullable = true, length = 15)
  private String phoneNumber;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserTransaction> userTransactions;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Category> categories;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Wallet> wallets;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Budget> budgets;

  @OneToMany(mappedBy = "createdGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ExpenseGroup> expenseGroups;

  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupMember> joinedGroup;

  @OneToMany(mappedBy = "inviter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupMember> invitedMembers;

  @OneToMany(mappedBy = "paidByUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupTransaction> groupTransactions;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TransactionSplit> transactionsSplits;

  @OneToMany(mappedBy = "payerUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Settlement> paySettlements;

  @OneToMany(mappedBy = "receiverUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Settlement> receiveSettlements;

  @OneToMany(mappedBy = "gotInvitation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupInvitation> groupInvitationReceived;

  @OneToMany(mappedBy = "invitedByUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupInvitation> groupInvitationsSent;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notification> notifications;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Alert> alerts;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<NotificationPreference> notificationPreferences;

  @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<NotificationChannel> notificationChannels;
}
