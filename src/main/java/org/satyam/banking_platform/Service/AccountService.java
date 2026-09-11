package org.satyam.banking_platform.Service;

import org.satyam.banking_platform.DTO.AccountResponse;
import org.satyam.banking_platform.Exception.BadRequestException;
import org.satyam.banking_platform.Exception.ForbiddenException;
import org.satyam.banking_platform.Exception.ResourceNotFoundException;
import org.satyam.banking_platform.Model.Account;
import org.satyam.banking_platform.Model.AccountStatus;
import org.satyam.banking_platform.Model.AccountType;
import org.satyam.banking_platform.Model.User;
import org.satyam.banking_platform.Repository.AccountRepository;
import org.satyam.banking_platform.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AccountService(
            AccountRepository accountRepository,
            UserRepository userRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // CREATE ACCOUNT
    // =========================================================

    @Transactional
    public AccountResponse createAccount(
            Long userId,
            AccountType accountType) {

        if (userId == null || userId <= 0) {
            throw new BadRequestException(
                    "Invalid user id"
            );
        }

        if (accountType == null) {
            throw new BadRequestException(
                    "Account type is required"
            );
        }

        // Get logged-in user
        User loggedInUser = getAuthenticatedUser();

        // User can create account only for himself
        if (!loggedInUser.getId().equals(userId)) {

            throw new ForbiddenException(
                    "You are not allowed to create an account for another user"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        Account account = new Account();

        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(accountType);
        account.setStatus(AccountStatus.ACTIVE);
        account.setAccountNumber(generateAccountNumber());

        Account savedAccount =
                accountRepository.save(account);

        return convertToResponse(savedAccount);
    }


    // =========================================================
    // GENERATE UNIQUE ACCOUNT NUMBER
    // =========================================================

    private String generateAccountNumber() {

        String accountNumber;

        do {

            long number =
                    1_000_000_000L
                            + secureRandom.nextLong(
                            9_000_000_000L
                    );

            accountNumber =
                    String.valueOf(number);

        } while (
                accountRepository
                        .findByAccountNumber(accountNumber)
                        .isPresent()
        );

        return accountNumber;
    }


    // =========================================================
    // GET ACCOUNT BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException(
                    "Invalid account id"
            );
        }

        Account account =
                accountRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found"
                                ));

        // Ownership check
        checkAccountOwnership(account);

        return convertToResponse(account);
    }


    // =========================================================
    // GET ALL ACCOUNTS OF LOGGED-IN USER
    // =========================================================

    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(Long userId) {

        if (userId == null || userId <= 0) {
            throw new BadRequestException(
                    "Invalid user id"
            );
        }

        User loggedInUser = getAuthenticatedUser();

        // User can only see his own accounts
        if (!loggedInUser.getId().equals(userId)) {

            throw new ForbiddenException(
                    "You are not allowed to view another user's accounts"
            );
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        return accountRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET BALANCE
    // =========================================================

    @Transactional(readOnly = true)
    public AccountResponse getBalance(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException(
                    "Invalid account id"
            );
        }

        Account account =
                accountRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found"
                                ));

        // Very important security check
        checkAccountOwnership(account);

        return convertToResponse(account);
    }


    // =========================================================
    // ACTIVATE / DEACTIVATE ACCOUNT
    // =========================================================

    @Transactional
    public void updateAccountStatus(
            Long id,
            AccountStatus status) {

        if (id == null || id <= 0) {
            throw new BadRequestException(
                    "Invalid account id"
            );
        }

        if (status == null) {
            throw new BadRequestException(
                    "Account status is required"
            );
        }

        Account account =
                accountRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found"
                                ));

        account.setStatus(status);

        accountRepository.save(account);
    }


    // =========================================================
    // GET AUTHENTICATED USER
    // =========================================================

    private User getAuthenticatedUser() {

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

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }


    // =========================================================
    // ACCOUNT OWNERSHIP CHECK
    // =========================================================

    private void checkAccountOwnership(Account account) {

        if (account.getUser() == null) {

            throw new ForbiddenException(
                    "Account owner not found"
            );
        }

        User loggedInUser =
                getAuthenticatedUser();

        if (!account.getUser()
                .getId()
                .equals(loggedInUser.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to access this account"
            );
        }
    }


    // =========================================================
    // ENTITY → DTO
    // =========================================================

    private AccountResponse convertToResponse(
            Account account) {

        AccountResponse response =
                new AccountResponse();

        response.setId(account.getId());

        response.setAccountNumber(
                account.getAccountNumber()
        );

        response.setBalance(
                account.getBalance()
        );

        response.setAccountType(
                account.getAccountType()
        );

        response.setAccountStatus(
                account.getStatus()
        );

        return response;
    }
}