package org.satyam.banking_platform.Controller;

import jakarta.validation.Valid;
import org.satyam.banking_platform.DTO.LoginRequest;
import org.satyam.banking_platform.DTO.LoginResponse;
import org.satyam.banking_platform.DTO.RegisterRequest;
import org.satyam.banking_platform.DTO.UserResponse;
import org.satyam.banking_platform.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
          @Valid @RequestBody RegisterRequest request) {

        UserResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);

    }
}