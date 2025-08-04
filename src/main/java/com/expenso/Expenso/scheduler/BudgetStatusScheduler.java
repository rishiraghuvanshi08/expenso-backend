package com.expenso.Expenso.scheduler;

import com.expenso.Expenso.entities.Budget;
import com.expenso.Expenso.enums.entity.BudgetStatus;
import com.expenso.Expenso.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetStatusScheduler {

  private final BudgetRepository budgetRepository;

  @Scheduled(cron = "0 30 18 * * *") // Runs every day at 12:00 AM IST (i.e., 6:30 PM UTC)
  public void updateBudgetStatuses() {
    LocalDate today = LocalDate.now();

    List<Budget> budgets = budgetRepository.findAllForStatusUpdate();

    for (Budget budget : budgets) {
      BudgetStatus previousStatus = budget.getStatus();
      boolean previousActive = budget.isActive();

      // Determine updated status
      if (today.isBefore(budget.getStartDate())) {
        budget.setStatus(BudgetStatus.UPCOMING);
        budget.setActive(false);
      } else if (today.isAfter(budget.getEndDate())) {
        budget.setStatus(BudgetStatus.EXPIRED);
        budget.setActive(false);
      } else {
        budget.setStatus(BudgetStatus.ACTIVE);
        budget.setActive(true);
      }

      // Save only if anything changed
      if (!budget.getStatus().equals(previousStatus) || budget.isActive() != previousActive) {
        budgetRepository.save(budget);
      }
    }
  }
}