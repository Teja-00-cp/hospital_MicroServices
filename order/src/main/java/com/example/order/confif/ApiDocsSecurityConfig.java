// package com.example.order.confif;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;
// import org.springframework.core.annotation.Order;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// /**
//  * Opens only the OpenAPI document and Swagger UI paths, and only under the "apitest" profile.
//  *
//  * <p>{@link SecurityConfig} ends with {@code anyRequest().authenticated()}, so springdoc would
//  * answer 401/403 and no tooling could read the document. This chain is registered ahead of that one
//  * but its {@code securityMatcher} covers nothing else, so every application request still falls
//  * through to the existing rules unchanged. Without the profile active this class is not loaded,
//  * which means the normal deployment is unaffected and the document stays closed.
//  */
// @Configuration
// @Profile("apitest")
// public class ApiDocsSecurityConfig {

//     @Bean
//     @Order(1)
//     public SecurityFilterChain apiDocsFilterChain(HttpSecurity http) throws Exception {
//         return http
//                 .securityMatcher("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
//                 .csrf(csrf -> csrf.disable())
//                 .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                 .build();
//     }
// }
