package com.example.stockscope.util;

import com.example.stockscope.exception.AdminOperationException;
import com.example.stockscope.model.Role;
import com.example.stockscope.model.User;

public class AdminValidationUtil {

    public static void validateAdminOperation(User targetUser, User adminUser) {
        if (targetUser == null) {
            throw new AdminOperationException("Target user not found");
        }

        // Prevent admin from modifying super admin
        if (targetUser.getRole() == Role.ADMIN && !adminUser.getId().equals(targetUser.getId())) {
            throw new AdminOperationException("Cannot modify another admin's account");
        }
    }

    public static void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new AdminOperationException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new AdminOperationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new AdminOperationException("Password must contain at least one special character");
        }
    }

    public static void validateUserStatus(User user) {
        if (!user.isEmailVerified()) {
            throw new AdminOperationException("User email is not verified");
        }
        if (!user.isEnabled()) {
            throw new AdminOperationException("User account is disabled");
        }
    }

    private AdminValidationUtil() {
        // Private constructor to prevent instantiation
    }
}