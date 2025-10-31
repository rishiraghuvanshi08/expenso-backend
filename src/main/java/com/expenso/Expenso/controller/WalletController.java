package com.expenso.Expenso.controller;

import com.expenso.Expenso.dto.wallet.WalletDetailsResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletRequestDTO;
import com.expenso.Expenso.dto.wallet.WalletResponseDTO;
import com.expenso.Expenso.dto.wallet.WalletUpdateDTO;
import com.expenso.Expenso.enums.response.WalletResponseMessage;
import com.expenso.Expenso.response.CustomResponse;
import com.expenso.Expenso.response.CustomResponseMessage;
import com.expenso.Expenso.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling all wallet-related operations such as
 * creating, updating, deleting, and retrieving wallet details.
 *
 * Base URL: /api/v1/wallets
 */
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet Management APIs", description = "Provides APIs to manage user wallets including creation, updates, and balance tracking.")
public class WalletController {

  private final WalletService walletService;

  /**
   * Retrieves all wallets belonging to the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @return List of wallets associated with the user.
   */
  @Operation(
    summary = "Get all wallets for user",
    description = "Fetches a list of all wallets created by the logged-in user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Wallet list fetched successfully")
  })
  @GetMapping
  public ResponseEntity<CustomResponse<List<WalletResponseDTO>>> getWallets(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_LIST_FETCH_SUCCESS.getMessage(), walletService.getAllWalletsForUser(userId)));
  }

  /**
   * Creates a new wallet for the authenticated user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @param dto    Wallet creation details including name and initial balance.
   * @return Created wallet details.
   */
  @Operation(
    summary = "Create a new wallet",
    description = "Allows the logged-in user to create a personal wallet."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Wallet created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input data"),
    @ApiResponse(responseCode = "404", description = "Resource user not found"),
  })
  @PostMapping
  public ResponseEntity<CustomResponse<WalletResponseDTO>> createWallet(@RequestAttribute("userId") Long userId, @Valid @RequestBody WalletRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(new CustomResponse<>(true, WalletResponseMessage.WALLET_CREATED_SUCCESS.getMessage(), walletService.createWallet(userId, dto)));
  }

  /**
   * Updates an existing wallet owned by the user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @param id     Wallet ID to update.
   * @param dto    Update data including wallet name (balance cannot be modified).
   * @return Updated wallet details.
   */
  @Operation(
    summary = "Update wallet details",
    description = "Updates wallet information such as name for the logged-in user."
  )
  @Parameter(name = "id", description = "Unique identifier of the wallet", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Wallet updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input data, Duplicate wallet found"),
    @ApiResponse(responseCode = "404", description = "Wallet or User not found")
  })
  @PutMapping("/{id}")
  public ResponseEntity<CustomResponse<WalletResponseDTO>> updateWallet(@RequestAttribute("userId") Long userId, @PathVariable Long id, @Valid @RequestBody WalletUpdateDTO dto) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_UPDATED_SUCCESS.getMessage(), walletService.updateWallet(userId, id, dto)));
  }

  /**
   * Deletes an existing wallet owned by the user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @param id     ID of the wallet to delete.
   * @return Success message after deletion.
   */
  @Operation(
    summary = "Delete a wallet",
    description = "Deletes a wallet owned by the authenticated user if it has no linked transactions."
  )
  @Parameter(name = "id", description = "Unique identifier of the wallet", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Wallet deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Wallet or User not found"),
    @ApiResponse(responseCode = "400", description = "Wallet cannot be deleted due to linked transactions")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<CustomResponseMessage> deleteWallet(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
    walletService.deleteWallet(userId, id);
    return ResponseEntity.ok(new CustomResponseMessage(true, WalletResponseMessage.WALLET_DELETED_SUCCESS.getMessage()));
  }

  /**
   * Retrieves the total balance across all wallets of the user.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @return Total wallet balance.
   */
  @Operation(
    summary = "Get total wallet balance",
    description = "Fetches the total balance across all wallets of the logged-in user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Total balance fetched successfully")
  })
  @GetMapping("/balance")
  public ResponseEntity<CustomResponse<Map<String, BigDecimal>>> getTotalBalance(@RequestAttribute("userId") Long userId) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_TOTAL_BALANCE_FETCH_SUCCESS.getMessage(), Map.of("totalBalance", walletService.getTotalBalance(userId))));
  }

  /**
   * Fetches wallet details along with its transaction history.
   *
   * @param userId ID of the authenticated user (injected via JWT filter).
   * @param id     ID of the wallet.
   * @return Wallet details including associated transactions.
   */
  @Operation(
    summary = "Get wallet details",
    description = "Retrieves full details of a wallet, including its transaction history."
  )
  @Parameter(name = "id", description = "Unique identifier of the wallet", required = true)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Wallet details fetched successfully"),
    @ApiResponse(responseCode = "404", description = "Wallet or User not found")
  })
  @GetMapping("/{id}/details")
  public ResponseEntity<CustomResponse<WalletDetailsResponseDTO>> getWalletDetails(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
    return ResponseEntity.ok(new CustomResponse<>(true, WalletResponseMessage.WALLET_FETCH_SUCCESS.getMessage(), walletService.getWalletDetailsWithTransactions(userId, id)));
  }
}