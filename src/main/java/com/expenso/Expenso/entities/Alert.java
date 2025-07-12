package com.expenso.Expenso.entities;

import com.expenso.Expenso.entities.embedded.AlertConfig;
import com.expenso.Expenso.entities.embedded.converter.AlertConfigConverter;
import com.expenso.Expenso.entities.enums.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "alert_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "alert_type", nullable = false, length = 30)
  private AlertType alertType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser appUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "expense_group_id", nullable = true)
  private ExpenseGroup expenseGroup;

  @Convert(converter = AlertConfigConverter.class)
  @Column(columnDefinition = "JSON")
  private AlertConfig config;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ScheduledAlert> scheduledAlerts;

}
