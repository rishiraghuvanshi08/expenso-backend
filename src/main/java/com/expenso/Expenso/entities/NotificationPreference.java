package com.expenso.Expenso.entities;

import com.expenso.Expenso.enums.entity.NotificationPreferencesNotify;
import com.expenso.Expenso.enums.entity.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
  name = "notification_preference",
  uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "notification_type", "notify_by"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_preference_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser appUser;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_type", nullable = false)
  private NotificationType notificationType;

  @Enumerated(EnumType.STRING)
  @Column(name = "notify_by", nullable = false)
  private NotificationPreferencesNotify notificationPreferencesNotify;

  @Column(name = "is_enabled", nullable = false)
  private Boolean isEnabled = true;

}
