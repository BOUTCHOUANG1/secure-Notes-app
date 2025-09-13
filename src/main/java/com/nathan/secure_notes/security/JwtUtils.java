package com.nathan.secure_notes.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;



/**
 * JwtUtils - Utility class for JSON Web Token (JWT) operations in Spring Security
 *
 * This class provides essential JWT functionality for authentication and authorization:
 * - Token generation
 * - Token validation
 * - Token extraction
 * - Username extraction from tokens
 *
 * The class uses HMAC-SHA algorithm for token signing and verification.
 */

/**
 * @Component marks this as a Spring component for automatic detection and injection
 */
@Component
public class JwtUtils {
    // Logger for debugging and error tracking
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Secret key used for signing JWT tokens
     * Loaded from application properties/configuration
     */
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    /**
     * Token expiration time in milliseconds
     * Loaded from application properties/configuration
     */
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Extracts JWT token from the Authorization header in HTTP request
     * Used in filter chains to process incoming requests
     *
     * @param request The HTTP request containing the Authorization header
     * @return The JWT token string without "Bearer " prefix, or null if not found
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove Bearer prefix
        }
        return null;
    }

    /**
     * Generates a new JWT token for a given user
     * Used during login/authentication process to create user tokens
     *
     * @param userDetails Spring Security user details containing username and authorities
     * @return A signed JWT token string
     */
    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    /**
     * Extracts username (subject) from a JWT token
     * Used in token validation and authorization processes
     *
     * @param token The JWT token string
     * @return The username (subject) contained in the token
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                        .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    /**
     * Generates a secret key from the base64-encoded jwtSecret
     * Used in token signing and verification processes
     *
     * @return A SecretKey instance for HMAC-SHA algorithms
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

/**
 * Validates a JWT token for authenticity and expiration
 *
 * This method performs comprehensive validation of a JWT token by:
 * 1. Verifying the token signature using the application's secret key
 * 2. Checking if the token has a valid format
 * 3. Ensuring the token hasn't expired
 * 4. Validating that the token type is supported
 *
 * Used in:
 * - Authentication filters to validate incoming requests
 * - Protected API endpoints to verify user access
 * - Token refresh flows to validate existing tokens
 *
 * Error cases handled:
 * - MalformedJwtException: When token has invalid format/signature
 * - ExpiredJwtException: When token has passed its expiration time
 * - UnsupportedJwtException: When token type is not supported
 * - IllegalArgumentException: When token string is empty/null
 *
 * @param authToken The JWT token string to validate
 * @return true if the token is valid, false otherwise
 */
public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
