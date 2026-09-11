package org.satyam.banking_platform.Controller;

import org.satyam.banking_platform.DTO.TransactionResponse;
import org.satyam.banking_platform.DTO.UserResponse;
import org.satyam.banking_platform.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                adminService.getAllUsers()
        );
    }

    // Get user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminService.getUserById(id)
        );
    }

    // Activate / Deactivate user
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        adminService.updateUserStatus(id, active);

        return ResponseEntity.ok("User status updated successfully");
    }

    // Get all transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {

        return ResponseEntity.ok(
                adminService.getAllTransactions()
        );
    }

    // Get transaction by ID
    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminService.getTransactionById(id)
        );
    }
}