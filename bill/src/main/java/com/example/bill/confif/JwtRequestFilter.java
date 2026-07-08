// package com.example.bill.confif;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import java.io.IOException;
// import java.util.Collections;

// @Component
// public class JwtRequestFilter extends OncePerRequestFilter {

//     private final JwtUtil jwtUtil;

//     public JwtRequestFilter(JwtUtil jwtUtil) {
//         this.jwtUtil = jwtUtil;
//     }

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//             throws ServletException, IOException {

//         final String authorizationHeader = request.getHeader("Authorization");

//         String username = null;
//         String jwt = null;

//         if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//             jwt = authorizationHeader.substring(7);
//             try {
//                 username = jwtUtil.extractUsername(jwt);
//             } catch (Exception e) {
//                 // Ignore invalid tokens; Spring Security will block the request automatically
//             }
//         }

//         if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
//             // Check signature and expiration
//             if (jwtUtil.validateTokenStateless(jwt)) {
                
//                 // Extract role directly from token (e.g., "PATIENT")
//                 String authority = jwtUtil.extractAuthority(jwt); 
                
//                 // Approve the request and attach the authority
//                 UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                         username, null, Collections.singletonList(new SimpleGrantedAuthority(authority)));
                
//                 authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                 SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//             }
//         }
//         chain.doFilter(request, response);
//     }
// }