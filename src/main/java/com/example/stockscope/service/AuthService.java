package com.example.stockscope.service;

import com.example.stockscope.dto.AuthResponse;
import com.example.stockscope.dto.LoginRequest;
import com.example.stockscope.dto.SignupRequest;
import com.example.stockscope.model.Role;
import com.example.stockscope.model.User;
import com.example.stockscope.repository.UserRepository;
import com.example.stockscope.security.JwtTokenProvider;
import com.example.stockscope.exception.AuthenticationException;
import com.example.stockscope.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Password validation pattern
    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    // Email validation pattern
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    @Transactional
    public AuthResponse login(LoginRequest request) {
        logger.debug("Attempting login for user: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);
        logger.info("User logged in successfully: {}", user.getEmail());

        return new AuthResponse(token, user);
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        logger.debug("Attempting signup for user: {}", request.getEmail());

        // Validate email format and uniqueness
        validateEmail(request.getEmail());

        // Validate password
        validatePassword(request.getPassword());

        // Create new user
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        try {
            user = userRepository.save(user);
            logger.info("New user created successfully: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error creating user account", e);
            throw new ValidationException("Error creating user account");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user);
    }

    @Transactional
    public void logout(String token) {
        // You could implement token blacklisting here if needed
        logger.info("User logged out successfully");
    }

    private void validateEmail(String email) {
        if (email == null || !emailPattern.matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new ValidationException("Email already registered");
        }
    }

    private void validatePassword(String password) {
        if (password == null || !pattern.matcher(password).matches()) {
            throw new ValidationException(
                    "Password must contain at least 8 characters, " +
                            "one uppercase letter, and one special character (@#$%^&+=!)"
            );
        }
    }
}