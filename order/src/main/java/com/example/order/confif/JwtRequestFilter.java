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
import java.util.List;

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

        // 1. Skip token validation completely for public and OpenAPI/Swagger endpoints
        if (requestURI.contains("/order/user/authenticate") || 
            requestURI.contains("/order/user/forgot") || 
            requestURI.contains("/order/user/addPatient") ||
            requestURI.contains("/v3/api-docs") ||
            requestURI.contains("/swagger-ui") ||
            requestURI.contains("/error")) {
            
            System.out.println("⚠️ DEBUG: Skipping JWT validation for public/OpenAPI endpoint: " + requestURI);
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
                
                String authority = jwtUtil.extractAuthority(jwt); 
                
                System.out.println("🛡️ SPRING SECURITY ROLE EXTRACTED: [" + authority + "]");
                
                if (authority == null || authority.trim().isEmpty()) {
                    System.out.println("⚠️ WARNING: Authority was null! Defaulting to PATIENT.");
                    authority = "PATIENT"; 
                }

                String rolePrefixed = authority.startsWith("ROLE_") ? authority : "ROLE_" + authority;
                String cleanRole = rolePrefixed.replace("ROLE_", "");

                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(rolePrefixed),
                    new SimpleGrantedAuthority(cleanRole)
                );
                
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}