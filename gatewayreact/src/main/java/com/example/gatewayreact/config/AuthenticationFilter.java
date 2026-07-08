package com.example.gatewayreact.config;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    // Define routes that DO NOT require a token (Login and Registration)
    private final List<String> openApiEndpoints = List.of(
            "/order/user/authenticate",
            "/order/user/addPatient",
            "/order/user/addDoctor",
            "/order/user"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Check if the route is public (Login/Register)
        boolean isPublicEndpoint = openApiEndpoints.stream()
                .anyMatch(uri -> request.getURI().getPath().contains(uri));

        if (isPublicEndpoint) {
            return chain.filter(exchange); // Let them pass without checking tokens
        }

        // 2. Check for the Authorization Header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 3. FIXED LOGIC: Proceed ONLY if the token is NOT expired (!)
            if (!jwtUtil.isTokenExpired(token)) {
                
                // 4. Extract Data securely from valid token
                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                String role = claims.get("authority", String.class);

                // 5. Mutate the request to inject headers for downstream services
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Name", username)
                        .header("X-User-Role", role)
                        .build();

                // Forward the newly mutated request downstream
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
        }

        // If execution reaches here, the token format was wrong, missing, or truly expired
        return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
    }

    // Handles the 401 Unauthorized rejection reactively
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    // Ensures this filter runs early in the Gateway pipeline
    @Override
    public int getOrder() {
        return -1; 
    }
}