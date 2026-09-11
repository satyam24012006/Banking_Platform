package org.satyam.banking_platform.Service;

import org.satyam.banking_platform.DTO.TransactionResponse;
import org.satyam.banking_platform.DTO.UserResponse;
import org.satyam.banking_platform.Exception.ResourceNotFoundException;
import org.satyam.banking_platform.Model.Transaction;
import org.satyam.banking_platform.Model.User;
import org.satyam.banking_platform.Repository.TransactionRepository;
import org.satyam.banking_platform.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AdminService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().
                stream().
                map(this::convertUserToResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return convertUserToResponse(user);
    }

    // Activate / Deactivate user
    public void updateUserStatus(Long id, boolean active) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        user.setActive(active);

        userRepository.save(user);
    }

    // Get all transactions
    public List<TransactionResponse> getAllTransactions() {

        return transactionRepository.findAll()
                .stream()
                .map(this::convertTransactionToResponse)
                .toList();
    }

    // Get transaction by ID
    public TransactionResponse getTransactionById(Long id) {

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Transaction not found"));

        return convertTransactionToResponse(transaction);
    }

    // User Entity → UserResponse
    private UserResponse convertUserToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());

        return response;
    }

    // Transaction Entity → TransactionResponse
    private TransactionResponse convertTransactionToResponse(
            Transaction transaction) {

        TransactionResponse response =
                new TransactionResponse();

        response.setId(transaction.getId());
        response.setSenderAccountNumber(
                transaction.getSenderAccount().getAccountNumber()
        );
        response.setRecipientAccountNumber(
                transaction.getRecipientAccount().getAccountNumber()
        );
        response.setAmount(transaction.getAmount());
        response.setTransactionType(
                transaction.getTransactionType()
        );
        response.setTransactionStatus(
                transaction.getStatus()
        );
        response.setTransactionDate(
                transaction.getTransactionDate()
        );

        return response;
    }
}