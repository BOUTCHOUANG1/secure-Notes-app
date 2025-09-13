package com.nathan.secure_notes.security;


import com.nathan.secure_notes.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
/**
 * Filter for processing JWT tokens in HTTP requests
 *
 * This filter intercepts incoming requests, extracts JWT tokens from the Authorization header,
 * validates them, and sets up Spring Security authentication if valid. It's used in the security
 * configuration to secure endpoints.
 *
 * Key features:
 * - Extracts JWT from Authorization header
 * - Validates token authenticity and expiration
 * - Sets up Spring Security authentication context
 *
 * Used in:
 * - Security configuration to secure endpoints
 * - Authentication process to validate user credentials
 * - Token refresh flows to maintain user sessions
 *
 * Error cases handled:
 * - Invalid or missing JWT token in Authorization header
 * - Expired JWT token
 * - Invalid token signature
 *
 * @see JwtUtils
 * @see UserDetailsServiceImpl
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Filters incoming HTTP requests to process JWT tokens
     *
     * This method is called for each incoming request. It extracts a JWT token from the Authorization header,
     * validates the token, and if valid, sets up Spring Security authentication. The filter then passes the request
     * to the next filter in the chain.
     *
     * @param request  The HttpServletRequest object containing the incoming request
     * @param response The HttpServletResponse object for the outgoing response
     * @param filterChain The FilterChain for invoking the next filter in the chain
     * @throws ServletException If an error occurs during the filter processing
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }
    /**
     * Parses JWT token from the Authorization header of an HTTP request
     *
     * This method extracts the JWT token from the "Authorization" header of the incoming HTTP request.
     * It expects the header to follow the format "Bearer <token>", where <token> is the actual JWT string.
     *
     * @param request The HttpServletRequest object containing the Authorization header
     * @return The JWT token string if present, null otherwise
     */
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromHeader(request);
        logger.debug("AuthTokenFilter.java: {}", jwt);
        return jwt;
    }
}

