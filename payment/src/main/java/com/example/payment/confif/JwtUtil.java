// package com.example.payment.confif;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.stereotype.Component;

// import javax.crypto.SecretKey;
// import java.util.Date;
// import java.util.function.Function;

// @Component
// public class JwtUtil {

//     // EXACT SAME KEY AS YOUR ORDER SERVICE
//     private final String SECRET_KEY = "your-hospital-management-system-super-secret-key"; 

//     private SecretKey getSigningKey() {
//         return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//     }

//     public String extractUsername(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     public String extractAuthority(String token) {
//         return extractAllClaims(token).get("authority", String.class);
//     }

//     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//         final Claims claims = extractAllClaims(token);
//         return claimsResolver.apply(claims);
//     }

//     private Claims extractAllClaims(String token) {
//         return Jwts.parser()
//                 .verifyWith(getSigningKey())
//                 .build()
//                 .parseSignedClaims(token)
//                 .getPayload();
//     }

//     // Stateless validation: Only checks if the signature is valid and it hasn't expired
//     public Boolean validateTokenStateless(String token) {
//         try {
//             return !extractClaim(token, Claims::getExpiration).before(new Date());
//         } catch (Exception e) {
//             return false;
//         }
//     }
// }