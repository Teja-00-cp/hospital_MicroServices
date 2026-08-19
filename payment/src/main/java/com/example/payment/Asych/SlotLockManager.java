package com.example.payment.Asych;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SlotLockManager {

    private final ConcurrentHashMap<String, Long> activeLocks = new ConcurrentHashMap<>();

    public boolean tryLockSlot(String lockKey, long ttlMillis) {
        long expiry = System.currentTimeMillis() + ttlMillis;
        Long existing = activeLocks.putIfAbsent(lockKey, expiry);
        
        if (existing == null || System.currentTimeMillis() > existing) {
            activeLocks.put(lockKey, expiry);
            return true; // Lock acquired
        }
        return false; // Already locked
    }

    public void releaseSlot(String lockKey) {
        activeLocks.remove(lockKey);
    }
}