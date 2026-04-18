package com.project.mdbm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestUri = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        System.out.println("\n===== JWT FILTER START =====");
        System.out.println("Request URI: " + requestUri);
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No valid Bearer token found. Continuing filter chain.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwtToken = authHeader.substring(7).trim();
            System.out.println("Extracted JWT Token: " + jwtToken);

            final String userEmail = jwtService.extractUsername(jwtToken);
            System.out.println("Extracted userEmail from token: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                System.out.println("Loaded userDetails username: " + userDetails.getUsername());

                boolean isValid = jwtService.isTokenValid(jwtToken, userDetails.getUsername());
                System.out.println("Is token valid: " + isValid);

                if (isValid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("Authentication set in SecurityContext.");
                } else {
                    System.out.println("Token validation failed.");
                }
            } else {
                System.out.println("User email is null or authentication already exists.");
            }

        } catch (Exception e) {
            System.out.println("JWT Filter Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        System.out.println("===== JWT FILTER END =====\n");
        filterChain.doFilter(request, response);
    }
}