package com.expenso.Expenso.entities;

import com.expenso.Expenso.entities.enums.NotificationChannelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
  name = "notification_channel",
  uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationChannel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_channel_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser appUser;

  @Enumerated(EnumType.STRING)
  @Column(name = "channel", nullable = false, length = 20)
  private NotificationChannelType notificationChannelType;

  @Column(name = "channel_address", nullable = false, columnDefinition = "TEXT")
  private String channelAddress;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

}
