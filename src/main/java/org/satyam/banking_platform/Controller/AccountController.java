package org.satyam.banking_platform.Controller;

import org.satyam.banking_platform.DTO.AccountResponse;
import org.satyam.banking_platform.Model.AccountStatus;
import org.satyam.banking_platform.Model.AccountType;
import org.satyam.banking_platform.Service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create account
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestParam Long userId,
            @RequestParam AccountType accountType) {

        AccountResponse response =
                accountService.createAccount(userId, accountType);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get account by ID
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                accountService.getAccountById(id)
        );
    }

    // Get all accounts of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getUserAccounts(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                accountService.getUserAccounts(userId)
        );
    }

    // Get account balance
    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> getBalance(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                accountService.getBalance(id)
        );
    }

    // Activate / Deactivate account
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {

        accountService.updateAccountStatus(id, status);

        return ResponseEntity.ok(
                "Account status updated successfully"
        );
    }
}