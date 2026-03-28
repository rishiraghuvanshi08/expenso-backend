package com.expenso.Expenso.service;

import com.expenso.Expenso.dto.wallet.*;
import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.UserTransaction;
import com.expenso.Expenso.entities.Wallet;
import com.expenso.Expenso.enums.entity.WalletStatus;
import com.expenso.Expenso.enums.entity.WalletType;
import com.expenso.Expenso.enums.response.AppUserResponseMessage;
import com.expenso.Expenso.enums.response.WalletResponseMessage;
import com.expenso.Expenso.exception.custom.InvalidRequestException;
import com.expenso.Expenso.exception.custom.ResourceNotFoundException;
import com.expenso.Expenso.repository.AppUserRepository;
import com.expenso.Expenso.repository.UserTransactionRepository;
import com.expenso.Expenso.repository.WalletRepository;
import com.expenso.Expenso.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

  @Mock private WalletRepository walletRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private UserTransactionRepository userTransactionRepository;

  @InjectMocks
  private WalletServiceImpl walletService;

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

  private Wallet buildWallet() {
    Wallet wallet = new Wallet();
    wallet.setId(1L);
    wallet.setName("Cash Wallet");
    wallet.setWalletType(WalletType.CASH);
    wallet.setBalance(BigDecimal.valueOf(5000));
    wallet.setStatus(WalletStatus.ACTIVE);
    return wallet;
  }

  // ================= READ =================

  @Test
  void getAllWalletsForUser_shouldReturnWalletList() {
    when(walletRepository.findAllByAppUserIdAndStatus(1L, WalletStatus.ACTIVE))
      .thenReturn(List.of(buildWallet()));

    List<WalletResponseDTO> result =
      walletService.getAllWalletsForUser(1L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Cash Wallet");
  }

  @Test
  void getTotalBalance_shouldReturnSum() {
    when(walletRepository.findTotalBalanceByUserId(1L))
      .thenReturn(BigDecimal.valueOf(10000));

    BigDecimal result = walletService.getTotalBalance(1L);

    assertThat(result).isEqualByComparingTo("10000");
  }

  @Test
  void getWalletDetailsWithTransactions_shouldReturnDetails() {
    Wallet wallet = buildWallet();

    when(walletRepository.findActiveWalletForActiveUser(1L, 1L))
      .thenReturn(Optional.of(wallet));

    when(userTransactionRepository.findActiveTransactionsByWalletAndUser(1L, 1L))
      .thenReturn(List.of());

    WalletDetailsResponseDTO result =
      walletService.getWalletDetailsWithTransactions(1L, 1L);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Cash Wallet");
  }

  // ================= CREATE =================

  @Test
  void createWallet_shouldCreateSuccessfully() {
    AppUser user = buildUser();

    WalletRequestDTO dto = new WalletRequestDTO();
    dto.setName("Savings");
    dto.setWalletType(WalletType.BANK);
    dto.setBalance(BigDecimal.valueOf(10000));

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(walletRepository.existsActiveWalletByNameAndTypeExcludingId(
      anyLong(), anyString(), any(), isNull()))
      .thenReturn(false);

    Wallet savedWallet = new Wallet();
    savedWallet.setId(1L);
    savedWallet.setName("Savings");
    savedWallet.setWalletType(WalletType.BANK);
    savedWallet.setBalance(BigDecimal.valueOf(10000));

    when(walletRepository.save(any(Wallet.class)))
      .thenReturn(savedWallet);

    WalletResponseDTO response =
      walletService.createWallet(1L, dto);

    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Savings");
  }

  @Test
  void createWallet_shouldThrow_whenDuplicateExists() {
    AppUser user = buildUser();

    WalletRequestDTO dto = new WalletRequestDTO();
    dto.setName("Savings");
    dto.setWalletType(WalletType.BANK);

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.of(user));

    when(walletRepository.existsActiveWalletByNameAndTypeExcludingId(
      anyLong(), anyString(), any(), isNull()))
      .thenReturn(true);

    assertThatThrownBy(() ->
                         walletService.createWallet(1L, dto))
      .isInstanceOf(InvalidRequestException.class)
      .hasMessageContaining(
        WalletResponseMessage.WALLET_ALREADY_EXISTS.getMessage()
      );
  }

  @Test
  void createWallet_shouldThrow_whenUserNotFound() {
    WalletRequestDTO dto = new WalletRequestDTO();

    when(appUserRepository.findById(1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         walletService.createWallet(1L, dto))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(
        AppUserResponseMessage.USER_NOT_FOUND.getMessage()
      );
  }

  // ================= UPDATE =================

  @Test
  void updateWallet_shouldUpdateSuccessfully() {
    Wallet wallet = buildWallet();

    WalletUpdateDTO dto = new WalletUpdateDTO();
    dto.setName("Updated Wallet");
    dto.setWalletType(WalletType.BANK);

    when(walletRepository.findActiveWalletForActiveUser(1L, 1L))
      .thenReturn(Optional.of(wallet));

    when(walletRepository.existsActiveWalletByNameAndTypeExcludingId(
      anyLong(), anyString(), any(), anyLong()))
      .thenReturn(false);

    when(walletRepository.save(any(Wallet.class)))
      .thenReturn(wallet);

    WalletResponseDTO response =
      walletService.updateWallet(1L, 1L, dto);

    assertThat(response.getName()).isEqualTo("Updated Wallet");
  }

  // ================= DELETE =================

  @Test
  void deleteWallet_shouldSoftDeleteSuccessfully() {
    Wallet wallet = buildWallet();
    wallet.setTransactions(List.of());

    when(walletRepository.findActiveWalletForActiveUser(1L, 1L))
      .thenReturn(Optional.of(wallet));

    walletService.deleteWallet(1L, 1L);

    assertThat(wallet.getStatus()).isEqualTo(WalletStatus.DELETED);
    verify(walletRepository).save(wallet);
  }

  @Test
  void deleteWallet_shouldThrow_whenTransactionsExist() {
    Wallet wallet = buildWallet();
    wallet.setTransactions(List.of(mock(UserTransaction.class)));

    when(walletRepository.findActiveWalletForActiveUser(1L, 1L))
      .thenReturn(Optional.of(wallet));

    assertThatThrownBy(() ->
                         walletService.deleteWallet(1L, 1L))
      .isInstanceOf(InvalidRequestException.class)
      .hasMessageContaining(
        WalletResponseMessage.WALLET_DELETION_RESTRICTED.getMessage()
      );
  }

  @Test
  void updateWallet_shouldThrow_whenWalletNotFound() {
    WalletUpdateDTO dto = new WalletUpdateDTO();

    when(walletRepository.findActiveWalletForActiveUser(1L, 1L))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
                         walletService.updateWallet(1L, 1L, dto))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(
        WalletResponseMessage.WALLET_NOT_FOUND.getMessage()
      );
  }
}