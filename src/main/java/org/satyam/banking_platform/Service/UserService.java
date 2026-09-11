package org.satyam.banking_platform.Service;

import org.satyam.banking_platform.DTO.UserResponse;
import org.satyam.banking_platform.Exception.BadRequestException;
import org.satyam.banking_platform.Exception.ConflictException;
import org.satyam.banking_platform.Exception.ResourceNotFoundException;
import org.satyam.banking_platform.Model.User;
import org.satyam.banking_platform.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET USER BY ID
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return convertToResponse(user);
    }


    // GET ALL USERS
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // UPDATE USER
    @Transactional
    public UserResponse updateUser(Long id, User userDetails) {

        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user id");
        }

        if (userDetails == null) {
            throw new BadRequestException("User details are required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Check duplicate email
        if (userDetails.getEmail() != null
                && !userDetails.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(userDetails.getEmail())) {

            throw new ConflictException("Email already exists");
        }

        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPhoneNumber() != null) {
            user.setPhoneNumber(userDetails.getPhoneNumber());
        }

        User updatedUser = userRepository.save(user);

        return convertToResponse(updatedUser);
    }


    // DELETE USER
    @Transactional
    public void deleteUser(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }


    // ENTITY → RESPONSE DTO
    private UserResponse convertToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());

        return response;
    }
}