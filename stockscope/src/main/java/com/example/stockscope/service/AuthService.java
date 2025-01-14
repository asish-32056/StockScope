package com.example.stockscope.service;

import com.example.stockscope.dto.AuthResponse;
import com.example.stockscope.dto.LoginRequest;
import com.example.stockscope.dto.SignupRequest;
import com.example.stockscope.model.ActivityLog;
import com.example.stockscope.model.Role;
import com.example.stockscope.model.User;
import com.example.stockscope.repository.UserRepository;
import com.example.stockscope.security.JwtTokenProvider;
import com.example.stockscope.exception.AuthenticationException;
import com.example.stockscope.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ActivityLogService activityLogService;
    private final HttpServletRequest request;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            ActivityLogService activityLogService,
            HttpServletRequest request) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.activityLogService = activityLogService;
        this.request = request;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logFailedLoginAttempt(user.getId());
            throw new AuthenticationException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new AuthenticationException("Account is disabled");
        }

        user.setLastLoginAt(LocalDateTime.now());
        user = userRepository.save(user);

        logSuccessfulLogin(user.getId());
        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(token, user);
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        validateSignupRequest(signupRequest);

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(signupRequest.getName().trim());
        user.setEmail(signupRequest.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        try {
            user = userRepository.save(user);
            logAccountCreation(user.getId());
        } catch (Exception e) {
            logger.error("Error creating user account", e);
            throw new ValidationException("Error creating user account");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user);
    }

    public void logout(String token) {
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            logLogout(userId);
        }
    }

    private void validateSignupRequest(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new ValidationException("Email already registered");
        }
        // Add additional validations as needed
    }

    private void logSuccessfulLogin(String userId) {
        activityLogService.logActivity(
                userId,
                ActivityLog.ActivityType.LOGIN,
                "Successful login",
                getClientInfo()
        );
    }

    private void logFailedLoginAttempt(String userId) {
        activityLogService.logActivity(
                userId,
                ActivityLog.ActivityType.LOGIN,
                "Failed login attempt",
                getClientInfo()
        );
    }

    private void logLogout(String userId) {
        activityLogService.logActivity(
                userId,
                ActivityLog.ActivityType.LOGOUT,
                "User logged out",
                getClientInfo()
        );
    }

    private void logAccountCreation(String userId) {
        activityLogService.logActivity(
                userId,
                ActivityLog.ActivityType.ACCOUNT_CREATION,
                "New account created",
                getClientInfo()
        );
    }

    private String getClientInfo() {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return String.format("IP: %s, User-Agent: %s", ipAddress, userAgent);
    }
}