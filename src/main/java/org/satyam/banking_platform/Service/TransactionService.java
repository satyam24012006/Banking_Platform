package org.satyam.banking_platform.Service;

import org.satyam.banking_platform.DTO.TransactionResponse;
import org.satyam.banking_platform.DTO.TransferRequest;
import org.satyam.banking_platform.Exception.BadRequestException;
import org.satyam.banking_platform.Exception.ForbiddenException;
import org.satyam.banking_platform.Exception.ResourceNotFoundException;
import org.satyam.banking_platform.Model.Account;
import org.satyam.banking_platform.Model.Transaction;
import org.satyam.banking_platform.Model.TransactionStatus;
import org.satyam.banking_platform.Model.TransactionType;
import org.satyam.banking_platform.Model.User;
import org.satyam.banking_platform.Repository.AccountRepository;
import org.satyam.banking_platform.Repository.TransactionRepository;
import org.satyam.banking_platform.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        // 1. Validate amount
        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Amount must be greater than zero"
            );
        }

        // 2. Get currently logged-in user
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new ForbiddenException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        // 3. Find sender account
        Account sender =
                accountRepository
                        .findByAccountNumber(
                                request.getSenderAccountNumber()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sender Account Not Found"
                                ));

        // 4. OWNERSHIP CHECK
        // Sender account must belong to logged-in user
        if (sender.getUser() == null ||
                !sender.getUser().getId().equals(user.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to transfer from this account"
            );
        }

        // 5. Find recipient account
        Account recipient =
                accountRepository
                        .findByAccountNumber(
                                request.getRecipientAccountNumber()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Recipient Account Not Found"
                                ));

        // 6. Sender and recipient cannot be same
        if (sender.getAccountNumber()
                .equals(recipient.getAccountNumber())) {

            throw new BadRequestException(
                    "Sender and recipient cannot be same"
            );
        }

        // 7. Check sender balance
        if (sender.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BadRequestException(
                    "Sender Balance Not Enough"
            );
        }

        // 8. Debit sender
        sender.setBalance(
                sender.getBalance()
                        .subtract(request.getAmount())
        );

        // 9. Credit recipient
        recipient.setBalance(
                recipient.getBalance()
                        .add(request.getAmount())
        );

        // 10. Save accounts
        accountRepository.save(sender);
        accountRepository.save(recipient);

        // 11. Create transaction record
        Transaction transaction = new Transaction();

        transaction.setSenderAccount(sender);
        transaction.setRecipientAccount(recipient);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(
                TransactionType.TRANSFER
        );
        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // 12. Return response
        return convertToResponse(savedTransaction);
    }

    // Get Transaction by Id
    public TransactionResponse getTransactionById(Long id) {

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction Not Found"
                                ));

        // Get logged-in user
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        // Check transaction ownership
        if (transaction.getSenderAccount() == null ||
                transaction.getSenderAccount().getUser() == null ||
                !transaction.getSenderAccount()
                        .getUser()
                        .getId()
                        .equals(user.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to view this transaction"
            );
        }

        return convertToResponse(transaction);
    }

    // Get Transaction History
    public List<TransactionResponse> getTransactionHistory() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        return transactionRepository.findAll()
                .stream()
                .filter(transaction ->

                        transaction.getSenderAccount() != null
                                && transaction.getSenderAccount()
                                .getUser() != null
                                && transaction.getSenderAccount()
                                .getUser()
                                .getId()
                                .equals(user.getId())
                )
                .map(this::convertToResponse)
                .toList();
    }

    // Entity → DTO
    private TransactionResponse convertToResponse(
            Transaction transaction) {

        TransactionResponse response =
                new TransactionResponse();

        response.setId(transaction.getId());

        response.setSenderAccountNumber(
                transaction.getSenderAccount()
                        .getAccountNumber()
        );

        response.setRecipientAccountNumber(
                transaction.getRecipientAccount()
                        .getAccountNumber()
        );

        response.setAmount(
                transaction.getAmount()
        );

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