package com.expenso.Expenso.entities;

import com.expenso.Expenso.entities.enums.GroupMemberRole;
import com.expenso.Expenso.entities.enums.GroupMemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_member_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "expense_group_id", nullable = false)
  private ExpenseGroup expenseGroup;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser member;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "invited_by_user_id", nullable = false)
  private AppUser inviter;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private GroupMemberRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private GroupMemberStatus groupMemberStatus;

  @Column(name = "joined_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime joinedAt;

}
