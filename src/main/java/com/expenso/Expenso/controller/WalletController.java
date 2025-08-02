package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.wallet.WalletDetailsResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletRequestDTO;
import com.expenso.Expenso.dto.wallet.WalletResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletUpdateDTO;
import com.expenso.Expenso.enums.response.WalletResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

  private final WalletService walletService;

  @GetMapping
  public ResponseEntity<CustomResponse<List<WalletResponseDTO>>> getWallets(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_LIST_FETCH_SUCCESS.getMessage(), walletService.getAllWalletsForUser(userId)));
  }

  @PostMapping
  public ResponseEntity<CustomResponse<WalletResponseDTO>> createWallet(@RequestAttribute("userId") Long userId, @Valid @RequestBody WalletRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(new CustomResponse<>(true, WalletResponseMessage.WALLET_CREATED_SUCCESS.getMessage(), walletService.createWallet(userId, dto)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomResponse<WalletResponseDTO>> updateWallet(@RequestAttribute("userId") Long userId, @PathVariable Long id, @Valid @RequestBody WalletUpdateDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_UPDATED_SUCCESS.getMessage(), walletService.updateWallet(userId, id, dto)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<CustomResponseMessage> deleteWallet(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
    walletService.deleteWallet(userId, id);
    return ResponseEntity.ok(new CustomResponseMessage(true, WalletResponseMessage.WALLET_DELETED_SUCCESS.getMessage()));
  }

  @GetMapping("/balance")
  public ResponseEntity<CustomResponse<Map<String, BigDecimal>>> getTotalBalance(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_TOTAL_BALANCE_FETCH_SUCCESS.getMessage(), Map.of("totalBalance", walletService.getTotalBalance(userId))));
  }

  @GetMapping("/{id}/details")
  public ResponseEntity<CustomResponse<WalletDetailsResponseDTO>> getWalletDetails(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_FETCH_SUCCESS.getMessage(), walletService.getWalletDetailsWithTransactions(userId, id)));
  }
}