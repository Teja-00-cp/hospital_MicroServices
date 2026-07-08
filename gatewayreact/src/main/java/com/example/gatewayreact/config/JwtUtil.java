package com.example.gatewayreact.config;// Adjust to your Gateway's package

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // THIS MUST MATCH THE ORDER MICROSERVICE EXACTLY!
    private final String SECRET_KEY = "your-hospital-management-system-super-secret-key"; 

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

   // Rename this to match what your Gateway's AuthenticationFilter expects!
    public boolean isTokenExpired(String token) {
        try {
            // Returns true if the expiration date is BEFORE right now
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // If parsing crashes or is malformed, treat it as expired/invalid
        }
    }
}