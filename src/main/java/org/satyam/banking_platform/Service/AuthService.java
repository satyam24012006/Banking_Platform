package org.satyam.banking_platform.Service;

import org.satyam.banking_platform.DTO.LoginRequest;
import org.satyam.banking_platform.DTO.LoginResponse;
import org.satyam.banking_platform.DTO.RegisterRequest;
import org.satyam.banking_platform.DTO.UserResponse;
import org.satyam.banking_platform.Exception.ResourceNotFoundException;
import org.satyam.banking_platform.Model.Role;
import org.satyam.banking_platform.Model.User;
import org.satyam.banking_platform.Repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // REGISTER
    public UserResponse register(RegisterRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        // BCrypt
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );
        user.setRole(Role.USER);
        user.setActive(true);

        User savedUser =
                userRepository.save(user);

        return convertToResponse(savedUser);
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String token =
                    jwtService.generateToken(userDetails);

            LoginResponse response =
                    new LoginResponse();

            response.setMessage("Login successful");
            response.setToken(token);

            return response;

        } catch (Exception ex) {

            throw new ResourceNotFoundException(
                    "Invalid email or password"
            );
        }
    }

    private UserResponse convertToResponse(User user) {

        UserResponse response =
                new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());

        return response;
    }
}