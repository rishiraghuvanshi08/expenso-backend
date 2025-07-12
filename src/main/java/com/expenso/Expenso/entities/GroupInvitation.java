package com.expenso.Expenso.entities;

import com.expenso.Expenso.entities.enums.GroupInvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_invitation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_invitation_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "expense_group_id", nullable = false)
  private ExpenseGroup expenseGroup;

//  @Column(name = "invited_user_email")
//  private String invitedUserEmail;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "invited_user_id", nullable = false)
  private AppUser gotInvitation;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "invited_by_user_id", nullable = false)
  private AppUser invitedByUser;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private GroupInvitationStatus groupInvitationStatus;

  @Column(name = "joined_at", updatable = false)
  private LocalDateTime joinedAt;

}
