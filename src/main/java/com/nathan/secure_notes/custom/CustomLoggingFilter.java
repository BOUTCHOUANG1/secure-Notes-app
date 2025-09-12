package com.nathan.secure_notes.custom;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("CustomLoggingFilter: Request URI: " + request.getRequestURI());
        System.out.println("CustomLoggingFilter: Request Method: " + request.getMethod());
        System.out.println("CustomLoggingFilter: Request Headers: " + request.getHeaderNames());
        System.out.println("CustomLoggingFilter: Request Parameters: " + request.getParameterNames());
        System.out.println("CustomLoggingFilter: Request Body: " + request.getReader());
        filterChain.doFilter(request, response);
        System.out.println("CustomLoggingFilter - Response Status - " + response.getStatus());
    }
}
