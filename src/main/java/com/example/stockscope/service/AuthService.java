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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final Map<String, String> tokenBlacklist = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private HttpServletRequest request;

    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Processing login request for user: {}", loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new AuthenticationException("Account is disabled");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = tokenProvider.generateToken(user);

        activityLogService.logActivity(
                user.getId(),
                ActivityLog.ActivityType.LOGIN,
                "User logged in successfully",
                getClientInfo());

        return new AuthResponse(token, user);
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        logger.info("Processing signup request for user: {}", signupRequest.getEmail());

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ValidationException("Email is already registered");
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);
        String token = tokenProvider.generateToken(user);

        activityLogService.logActivity(
                user.getId(),
                ActivityLog.ActivityType.ACCOUNT_CREATION,
                "New account created",
                getClientInfo());

        return new AuthResponse(token, user);
    }

    public void logout(String token) {
        if (token != null) {
            String userId = tokenProvider.getUserIdFromToken(token);
            tokenBlacklist.put(token, LocalDateTime.now().toString());

            activityLogService.logActivity(
                    userId,
                    ActivityLog.ActivityType.LOGOUT,
                    "User logged out",
                    getClientInfo());
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || tokenBlacklist.containsKey(refreshToken)) {
            throw new AuthenticationException("Invalid or expired refresh token");
        }

        String userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!user.isEnabled()) {
            throw new AuthenticationException("Account is disabled");
        }

        String newToken = tokenProvider.generateToken(user);

        activityLogService.logActivity(
                userId,
                ActivityLog.ActivityType.TOKEN_REFRESH,
                "Token refreshed",
                getClientInfo());

        return new AuthResponse(newToken, user);
    }

    private String getClientInfo() {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return String.format("IP: %s, User-Agent: %s", ipAddress, userAgent);
    }
}