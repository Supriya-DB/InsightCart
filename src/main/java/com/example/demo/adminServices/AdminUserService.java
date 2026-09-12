package com.example.demo.adminServices;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.User;
import com.example.demo.entities.Role;
import com.example.demo.repositories.JWTTokenRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    private final JWTTokenRepository jwtTokenRepository;


    public AdminUserService(
            UserRepository userRepository,
            JWTTokenRepository jwtTokenRepository) {

        this.userRepository = userRepository;

        this.jwtTokenRepository =
                jwtTokenRepository;
    }


    // =========================================================
    // MODIFY USER
    // =========================================================

    @Transactional
    public User modifyUser(
            Integer userId,
            String username,
            String email,
            String role) {


        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }


        Optional<User> userOptional =
                userRepository.findById(userId);


        if (userOptional.isEmpty()) {

            throw new IllegalArgumentException(
                    "User not found"
            );
        }


        User existingUser =
                userOptional.get();


        // -----------------------------------------------------
        // USERNAME
        // -----------------------------------------------------

        if (username != null &&
                !username.trim().isEmpty()) {

            existingUser.setUsername(
                    username.trim()
            );
        }


        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        if (email != null &&
                !email.trim().isEmpty()) {

            existingUser.setEmail(
                    email.trim()
            );
        }


        // -----------------------------------------------------
        // ROLE
        // -----------------------------------------------------

        if (role != null &&
                !role.trim().isEmpty()) {

            try {

                existingUser.setRole(
                        Role.valueOf(
                                role.trim().toUpperCase()
                        )
                );

            } catch (IllegalArgumentException e) {

                throw new IllegalArgumentException(
                        "Invalid role: " + role
                );
            }
        }


        // -----------------------------------------------------
        // DELETE OLD JWT TOKENS
        // -----------------------------------------------------

        jwtTokenRepository.deleteByUserId(
                userId
        );


        // -----------------------------------------------------
        // SAVE USER
        // -----------------------------------------------------

        return userRepository.save(
                existingUser
        );
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    public User getUserById(Integer userId) {

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }


        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "User not found"
                        )
                );
    }
}