package com.example.order.confif;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

            String requestURI = request.getRequestURI();
                // System.out.println("🔍 DEBUG: Incoming request URI: " + requestURI);
    // 🚨 ADD THIS CONDITIONAL CHECK AT THE VERY TOP OF THE FILTER:
    if (requestURI.contains("/order/user/authenticate") || 
        requestURI.contains("/order/user/forgot") || 
        requestURI.contains("/order/user/addPatient")) {
        System.out.println("⚠️ DEBUG: Skipping JWT validation for public endpoint: " + requestURI);
        // Skip token validation completely and pass the request along!
        chain.doFilter(request, response);
        return;
    }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.out.println("❌ DEBUG: Token extraction failed: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtUtil.isTokenExpired(jwt)) {
                
                // 1. Extract the authority from the token claims
                String authority = jwtUtil.extractAuthority(jwt); 
                
                // 2. PRINT IT TO THE CONSOLE to see what is actually inside the token
                System.out.println("🛡️ SPRING SECURITY ROLE EXTRACTED: [" + authority + "]");
                
                // 3. FALLBACK: If the token doesn't have a role, force it so it doesn't crash
                if (authority == null || authority.trim().isEmpty()) {
                    System.out.println("⚠️ WARNING: Authority was null! Defaulting to PATIENT.");
                    authority = "PATIENT"; 
                }
                
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority(authority)));
                
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}