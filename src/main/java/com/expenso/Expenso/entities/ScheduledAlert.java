package com.expenso.Expenso.entities;

import com.expenso.Expenso.entities.embedded.AlertResult;
import com.expenso.Expenso.entities.embedded.converter.AlertResultConverter;
import com.expenso.Expenso.enums.entity.ScheduledAlertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledAlert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "scheduled_alert_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "alert_id", nullable = false)
  private Alert alert;

  @Column(name = "next_run_at", nullable = true)
  private LocalDateTime nextRunAt;

  @Column(name = "last_checked_at", nullable = true)
  private LocalDateTime lastCheckedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ScheduledAlertStatus status;

  @Convert(converter = AlertResultConverter.class)
  @Column(name = "last_result", columnDefinition = "JSON")
  private AlertResult lastResult;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

}
