package com.example.stockscope.security;

import com.example.stockscope.exception.AuthenticationException;
import com.example.stockscope.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            throw new AuthenticationException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            throw new AuthenticationException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            throw new AuthenticationException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            throw new AuthenticationException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
            throw new AuthenticationException("JWT claims string is empty");
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}