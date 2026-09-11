package org.satyam.banking_platform.Controller;

import jakarta.validation.Valid;
import org.satyam.banking_platform.Service.TransactionService;
import org.satyam.banking_platform.DTO.TransactionResponse;
import org.satyam.banking_platform.DTO.TransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }
    //Get Transaction By id
    @GetMapping("/{id}")
        public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
        }

        //Get All Transactions
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory() {
       return ResponseEntity.ok(transactionService.getTransactionHistory());
    }
}
