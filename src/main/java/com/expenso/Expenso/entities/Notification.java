package com.expenso.Expenso.entities;

import com.expenso.Expenso.entities.embedded.NotificationMetadata;
import com.expenso.Expenso.entities.embedded.converter.DeliveryChannelConverter;
import com.expenso.Expenso.entities.embedded.converter.NotificationMetadataConverter;
import com.expenso.Expenso.entities.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser appUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "expense_group_id")
  private ExpenseGroup expenseGroup;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_type", nullable = false, length = 30)
  private NotificationType notificationType;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String message;

  @Convert(converter = NotificationMetadataConverter.class)
  @Column(name = "meta_data", columnDefinition = "JSON", nullable = false)
  private NotificationMetadata metadata;

  @Convert(converter = DeliveryChannelConverter.class)
  @Column(name = "delivery_channels", columnDefinition = "JSON", nullable = false)
  private List<String> deliveryChannels;

  @Column(name = "is_read", nullable = false)
  private Boolean isRead = false;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
