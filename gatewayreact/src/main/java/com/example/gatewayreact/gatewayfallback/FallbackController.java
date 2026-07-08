package com.example.gatewayreact.gatewayfallback; // Use your actual Gateway package name!

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class FallbackController {

    // Handles failures for the Payment Service
    @RequestMapping("/gateway-fallback/payment")
    public ResponseEntity<?> paymentFallback() {
        System.out.println("⚠️ GATEWAY CIRCUIT BREAKER TRIPPED for Payment Service!");
        // We return an empty list so the frontend jQuery doesn't crash!
        return ResponseEntity.ok(Collections.emptyList());
    }

    // You can also add safety nets for your other services while we are here:
    @RequestMapping("/gateway-fallback/welcome")
    public ResponseEntity<?> welcomeFallback() {
        return ResponseEntity.status(503).body("Welcome Service is currently down.");
    }

    @RequestMapping("/gateway-fallback/bill")
    public ResponseEntity<?> billFallback() {
        return ResponseEntity.status(503).body("Bill Service is currently down.");
    }
}