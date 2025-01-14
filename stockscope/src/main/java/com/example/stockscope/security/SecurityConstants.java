package com.example.stockscope.security;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api/public/**"
    };
    public static final String[] ADMIN_URLS = {
            "/api/admin/**"
    };
    public static final String[] USER_URLS = {
            "/api/user/**"
    };

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
}