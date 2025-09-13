package com.nathan.secure_notes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Entry Point for JWT Security
 *
 * This class implements Spring Security's AuthenticationEntryPoint interface to handle
 * unauthorized access attempts to secured resources.
 *
 * Purpose:
 * - Provides a centralized way to handle authentication failures in the application
 * - Returns a proper JSON response when unauthenticated users try to access protected endpoints
 * - Customizes the unauthorized error response format
 *
 * Use Cases:
 * 1. When an unauthenticated user tries to access a protected resource
 * 2. When a JWT token is invalid or expired
 * 3. When authentication credentials are missing or incorrect
 *
 * Response Format:
 * Returns a JSON object containing:
 * - status: HTTP 401 Unauthorized status code
 * - error: Error description ("Unauthorized")
 * - message: Detailed error message from the AuthenticationException
 * - path: The requested URL path that triggered the error
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Method called when an AuthenticationException is thrown during authentication process
     *
     * @param request The HTTP request that resulted in an AuthenticationException
     * @param response The HTTP response to be modified
     * @param authException The exception that triggered this handler
     * @throws IOException If an I/O error occurs while writing the response
     * @throws ServletException If a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

}

