package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.usertransaction.*;
import com.expenso.Expenso.entities.*;
import com.expenso.Expenso.enums.entity.TransactionType;
import com.expenso.Expenso.enums.request.TransactionGroupBy;
import com.expenso.Expenso.enums.response.UserTransactionResponseMessage;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.repository.*;
import com.expenso.Expenso.service.impl.UserTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTransactionServiceTest {

  @Mock private UserTransactionRepository userTransactionRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private WalletRepository walletRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private BudgetRepository budgetRepository;

  @InjectMocks
  private UserTransactionServiceImpl userTransactionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private AppUser buildUser() {
    return AppUser.builder()
                  .id(1L)
                  .name("Rishi")
                  .email("rishi@gmail.com")
                  .build();
  }

  private Category buildCategory() {
    Category category = new Category();
    category.setId(1L);
    category.setName("Food");
    return category;
  }

  private Wallet buildWallet() {
    Wallet wallet = new Wallet();
    wallet.setId(1L);
    wallet.setName("Cash");
    wallet.setBalance(BigDecimal.valueOf(1000));
    return wallet;
  }

  private UserTransaction buildTransaction() {
    UserTransaction tx = new UserTransaction();
    tx.setId(1L);
    tx.setCategory(buildCategory());
    tx.setWallet(buildWallet());
    tx.setAmount(BigDecimal.valueOf(200));
    tx.setTransactionType(TransactionType.EXPENSE);
    tx.setDate(LocalDate.now());
    tx.setNote("Lunch");
    tx.setIsDeleted(false);
    return tx;
  }

  // ================= CREATE =================

  @Test
  void createTransaction_shouldCreateExpenseAndDecreaseWalletBalance() {
    AppUser user = buildUser();
    Category category = buildCategory();
    Wallet wallet = buildWallet();

    UserTransactionRequestDTO dto = new UserTransactionRequestDTO();
    dto.setCategoryId(1L);
    dto.setWalletId(1L);
    dto.setAmount(BigDecimal.valueOf(200));
    dto.setTransactionType(TransactionType.EXPENSE);
    dto.setDate(LocalDate.now());
    dto.setNote("Lunch");

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(walletRepository.findById(1L))
      .thenReturn(Optional.of(wallet));

    when(userTransactionRepository.save(any(UserTransaction.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    UserTransactionResponseDTO response =
      userTransactionService.createTransaction(1L, dto);

    assertThat(response).isNotNull();
    assertThat(wallet.getBalance()).isEqualByComparingTo("800");

    verify(walletRepository).save(wallet);
    verify(userTransactionRepository).save(any(UserTransaction.class));
  }

  @Test
  void createTransaction_shouldCreateIncomeAndIncreaseWalletBalance() {
    AppUser user = buildUser();
    Category category = buildCategory();
    Wallet wallet = buildWallet();

    UserTransactionRequestDTO dto = new UserTransactionRequestDTO();
    dto.setCategoryId(1L);
    dto.setWalletId(1L);
    dto.setAmount(BigDecimal.valueOf(500));
    dto.setTransactionType(TransactionType.INCOME);
    dto.setDate(LocalDate.now());

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(walletRepository.findById(1L))
      .thenReturn(Optional.of(wallet));

    when(userTransactionRepository.save(any(UserTransaction.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    userTransactionService.createTransaction(1L, dto);

    assertThat(wallet.getBalance()).isEqualByComparingTo("1500");
  }

  // ================= UPDATE =================

  @Test
  void updateTransaction_shouldUpdateSuccessfully() {
    UserTransaction tx = buildTransaction();
    Category newCategory = buildCategory();

    UserTransactionUpdateDTO dto = new UserTransactionUpdateDTO();
    dto.setCategoryId(1L);
    dto.setNote("Updated Note");
    dto.setDate(LocalDate.now());

    when(userTransactionRepository
           .findByIdAndAppUserIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(Optional.of(tx));

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(newCategory));

    when(userTransactionRepository.save(any(UserTransaction.class)))
      .thenReturn(tx);

    UserTransactionResponseDTO response =
      userTransactionService.updateTransaction(1L, 1L, dto);

    assertThat(response).isNotNull();
    assertThat(response.getNote()).isEqualTo("Updated Note");
  }

  @Test
  void updateTransaction_shouldThrow_whenTransactionNotFound() {
    UserTransactionUpdateDTO dto = new UserTransactionUpdateDTO();

    when(userTransactionRepository
           .findByIdAndAppUserIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         userTransactionService.updateTransaction(1L, 1L, dto))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(
        UserTransactionResponseMessage.TRANSACTION_NOT_FOUND.getMessage()
      );
  }

  // ================= DELETE =================

  @Test
  void deleteTransaction_shouldReverseExpenseAndSoftDelete() {
    UserTransaction tx = buildTransaction();
    Wallet wallet = tx.getWallet();

    when(userTransactionRepository
           .findByIdAndAppUserIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(Optional.of(tx));

    userTransactionService.deleteTransaction(1L, 1L);

    assertThat(wallet.getBalance()).isEqualByComparingTo("1200");
    assertThat(tx.getIsDeleted()).isTrue();

    verify(walletRepository).save(wallet);
    verify(userTransactionRepository).save(tx);
  }

  @Test
  void deleteTransaction_shouldReverseIncomeAndSoftDelete() {
    UserTransaction tx = buildTransaction();
    tx.setTransactionType(TransactionType.INCOME);

    Wallet wallet = tx.getWallet();
    wallet.setBalance(BigDecimal.valueOf(1000));

    when(userTransactionRepository
           .findByIdAndAppUserIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(Optional.of(tx));

    userTransactionService.deleteTransaction(1L, 1L);

    assertThat(wallet.getBalance()).isEqualByComparingTo("800");
    assertThat(tx.getIsDeleted()).isTrue();
  }

  // ================= GET ALL =================

  @Test
  void getAllTransactions_shouldReturnList() {
    when(userTransactionRepository
           .findByAppUserIdAndIsDeletedFalse(1L))
      .thenReturn(List.of(buildTransaction()));

    List<UserTransactionResponseDTO> result =
      userTransactionService.getAllTransactions(1L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCategoryName()).isEqualTo("Food");
  }

  // ================= GET BY CATEGORY =================

  @Test
  void getTransactionsByCategory_shouldReturnList() {
    UserTransaction tx = buildTransaction();

    when(userTransactionRepository
           .findByAppUserIdAndCategoryIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(List.of(tx));

    List<UserTransactionResponseDTO> result =
      userTransactionService.getTransactionsByCategory(1L, 1L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCategoryName()).isEqualTo("Food");
    assertThat(result.get(0).getAmount())
      .isEqualByComparingTo("200");
  }

  @Test
  void getTransactionsByCategory_shouldReturnEmptyList() {
    when(userTransactionRepository
           .findByAppUserIdAndCategoryIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(List.of());

    List<UserTransactionResponseDTO> result =
      userTransactionService.getTransactionsByCategory(1L, 1L);

    assertThat(result).isEmpty();
  }

  // ================= GET BY BUDGET =================

  @Test
  void getTransactionsByBudget_shouldReturnList() {
    Budget budget = new Budget();
    budget.setId(1L);
    budget.setCategory(buildCategory());
    budget.setStartDate(LocalDate.now().minusDays(5));
    budget.setEndDate(LocalDate.now().plusDays(5));

    UserTransaction tx = buildTransaction();

    when(budgetRepository.findByIdAndAppUserId(1L, 1L))
      .thenReturn(Optional.of(budget));

    when(userTransactionRepository
           .findByAppUserIdAndCategoryIdAndDateBetweenAndIsDeletedFalse(
             anyLong(), anyLong(), any(), any()))
      .thenReturn(List.of(tx));

    List<UserTransactionResponseDTO> result =
      userTransactionService.getTransactionsByBudget(1L, 1L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCategoryName()).isEqualTo("Food");
  }

  @Test
  void getTransactionsByBudget_shouldThrow_whenBudgetNotFound() {
    when(budgetRepository.findByIdAndAppUserId(1L, 1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         userTransactionService.getTransactionsByBudget(1L, 1L))
      .isInstanceOf(ResourceNotFoundException.class);
  }

  // ================= SUMMARY =================

  @Test
  void getSummary_shouldReturnCategorySummary() {
    TransactionSummaryDTO summary = TransactionSummaryDTO.builder()
                                                         .groupBy(TransactionGroupBy.CATEGORY)
                                                         .label("Food")
                                                         .incomeTotal(BigDecimal.valueOf(1000))
                                                         .expenseTotal(BigDecimal.ZERO)
                                                         .balance(BigDecimal.valueOf(1000))
                                                         .build();

    when(userTransactionRepository.getSummaryByCategory(1L))
      .thenReturn(List.of(summary));

    List<TransactionSummaryDTO> result =
      userTransactionService.getSummary(
        1L,
        TransactionGroupBy.CATEGORY
      );

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getLabel()).isEqualTo("Food");
  }

  @Test
  void getSummary_shouldReturnWalletSummary() {
    TransactionSummaryDTO summary = TransactionSummaryDTO.builder()
                                                         .groupBy(TransactionGroupBy.WALLET)
                                                         .label("Cash")
                                                         .incomeTotal(BigDecimal.valueOf(5000))
                                                         .expenseTotal(BigDecimal.ZERO)
                                                         .balance(BigDecimal.valueOf(5000))
                                                         .build();

    when(userTransactionRepository.getSummaryByWallet(1L))
      .thenReturn(List.of(summary));

    List<TransactionSummaryDTO> result =
      userTransactionService.getSummary(
        1L,
        TransactionGroupBy.WALLET
      );

    assertThat(result).hasSize(1);
  }

  @Test
  void getSummary_shouldReturnMonthSummary() {
    TransactionSummaryDTO summary = TransactionSummaryDTO.builder()
                                                         .groupBy(TransactionGroupBy.MONTH)
                                                         .label("March 2026")
                                                         .incomeTotal(BigDecimal.valueOf(2000))
                                                         .expenseTotal(BigDecimal.ZERO)
                                                         .balance(BigDecimal.valueOf(2000))
                                                         .build();

    when(userTransactionRepository.getSummaryByMonth(1L))
      .thenReturn(List.of(summary));

    List<TransactionSummaryDTO> result =
      userTransactionService.getSummary(
        1L,
        TransactionGroupBy.MONTH
      );

    assertThat(result).hasSize(1);
  }

  // ================= STATS =================

  @Test
  void getStats_shouldReturnCalculatedStats() {
    List<Object[]> statsData = java.util.Arrays.asList(
      new Object[]{2026, 3, BigDecimal.valueOf(10000), BigDecimal.valueOf(3000)},
      new Object[]{2026, 4, BigDecimal.valueOf(5000), BigDecimal.valueOf(1000)}
    );

    when(userTransactionRepository.getMonthlyStats(1L))
      .thenReturn(statsData);

    TransactionStatsDTO result =
      userTransactionService.getStats(1L);

    assertThat(result.getTotalIncome())
      .isEqualByComparingTo("15000");

    assertThat(result.getTotalExpense())
      .isEqualByComparingTo("4000");

    assertThat(result.getNetBalance())
      .isEqualByComparingTo("11000");

    assertThat(result.getMonthlyTotals()).hasSize(2);
    assertThat(result.getMonthlyTotals())
      .containsKey("March 2026")
      .containsKey("April 2026");
  }

  @Test
  void getStats_shouldReturnZeroStats_whenNoData() {
    when(userTransactionRepository.getMonthlyStats(1L))
      .thenReturn(List.of());

    TransactionStatsDTO result =
      userTransactionService.getStats(1L);

    assertThat(result.getTotalIncome())
      .isEqualByComparingTo("0");

    assertThat(result.getTotalExpense())
      .isEqualByComparingTo("0");

    assertThat(result.getNetBalance())
      .isEqualByComparingTo("0");

    assertThat(result.getMonthlyTotals()).isEmpty();
  }

  // ================= CREATE NEGATIVE =================

  @Test
  void createTransaction_shouldThrow_whenUserNotFound() {
    UserTransactionRequestDTO dto = new UserTransactionRequestDTO();
    dto.setCategoryId(1L);

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         userTransactionService.createTransaction(1L, dto))
      .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void createTransaction_shouldThrow_whenCategoryNotFound() {
    AppUser user = buildUser();

    UserTransactionRequestDTO dto = new UserTransactionRequestDTO();
    dto.setCategoryId(1L);

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         userTransactionService.createTransaction(1L, dto))
      .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void createTransaction_shouldThrow_whenWalletNotFound() {
    AppUser user = buildUser();
    Category category = buildCategory();

    UserTransactionRequestDTO dto = new UserTransactionRequestDTO();
    dto.setCategoryId(1L);
    dto.setWalletId(1L);
    dto.setAmount(BigDecimal.valueOf(100));
    dto.setTransactionType(TransactionType.EXPENSE);

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(category));

    when(walletRepository.findById(1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         userTransactionService.createTransaction(1L, dto))
      .isInstanceOf(ResourceNotFoundException.class);
  }

  // ================= UPDATE NEGATIVE =================

  @Test
  void updateTransaction_shouldThrow_whenCategoryNotFound() {
    UserTransaction tx = buildTransaction();

    UserTransactionUpdateDTO dto = new UserTransactionUpdateDTO();
    dto.setCategoryId(99L);

    when(userTransactionRepository
           .findByIdAndAppUserIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(Optional.of(tx));

    when(categoryRepository.findById(99L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         userTransactionService.updateTransaction(1L, 1L, dto))
      .isInstanceOf(ResourceNotFoundException.class);
  }

  // ================= DELETE NEGATIVE =================

  @Test
  void deleteTransaction_shouldThrow_whenTransactionNotFound() {
    when(userTransactionRepository
           .findByIdAndAppUserIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         userTransactionService.deleteTransaction(1L, 1L))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(
        UserTransactionResponseMessage.TRANSACTION_NOT_FOUND.getMessage()
      );
  }

  @Test
  void deleteTransaction_shouldSoftDeleteWithoutWallet() {
    UserTransaction tx = buildTransaction();
    tx.setWallet(null);

    when(userTransactionRepository
           .findByIdAndAppUserIdAndIsDeletedFalse(1L, 1L))
      .thenReturn(Optional.of(tx));

    userTransactionService.deleteTransaction(1L, 1L);

    assertThat(tx.getIsDeleted()).isTrue();

    verify(walletRepository, never()).save(any());
    verify(userTransactionRepository).save(tx);
  }

  // ================= STATS EDGE =================

  @Test
  void getStats_shouldHandleIntegerAndDoubleValues() {
    List<Object[]> statsData = java.util.Collections.singletonList(
      new Object[]{2026, 3, 1000, 200.5}
    );

    when(userTransactionRepository.getMonthlyStats(1L))
      .thenReturn(statsData);

    TransactionStatsDTO result =
      userTransactionService.getStats(1L);

    assertThat(result.getTotalIncome())
      .isEqualByComparingTo("1000");

    assertThat(result.getTotalExpense())
      .isEqualByComparingTo("200.5");

    assertThat(result.getNetBalance())
      .isEqualByComparingTo("799.5");
  }

  // ================= SUMMARY EDGE =================

  @Test
  void getSummary_shouldReturnDateSummary() {
    TransactionSummaryDTO summary = TransactionSummaryDTO.builder()
                                                         .groupBy(TransactionGroupBy.DATE)
                                                         .label("2026-03-28")
                                                         .incomeTotal(BigDecimal.valueOf(500))
                                                         .expenseTotal(BigDecimal.ZERO)
                                                         .balance(BigDecimal.valueOf(500))
                                                         .build();

    when(userTransactionRepository.getSummaryByDate(1L))
      .thenReturn(List.of(summary));

    List<TransactionSummaryDTO> result =
      userTransactionService.getSummary(
        1L,
        TransactionGroupBy.DATE
      );

    assertThat(result).hasSize(1);
  }

  @Test
  void getSummary_shouldReturnYearSummary() {
    TransactionSummaryDTO summary = TransactionSummaryDTO.builder()
                                                         .groupBy(TransactionGroupBy.YEAR)
                                                         .label("2026")
                                                         .incomeTotal(BigDecimal.valueOf(12000))
                                                         .expenseTotal(BigDecimal.ZERO)
                                                         .balance(BigDecimal.valueOf(12000))
                                                         .build();

    when(userTransactionRepository.getSummaryByYear(1L))
      .thenReturn(List.of(summary));

    List<TransactionSummaryDTO> result =
      userTransactionService.getSummary(
        1L,
        TransactionGroupBy.YEAR
      );

    assertThat(result).hasSize(1);
  }
}