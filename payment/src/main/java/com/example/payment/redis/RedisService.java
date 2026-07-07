package com.example.payment.redis;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisService {

    // This HashMap acts as our "Fake Redis" database in memory
    private final Map<String, LockEntry> cache = new ConcurrentHashMap<>();

    // Internal class to keep track of the value and when it expires
    private static class LockEntry {
        String value;
        long expirationTimeMs;

        LockEntry(String value, long expirationTimeMs) {
            this.value = value;
            this.expirationTimeMs = expirationTimeMs;
        }
    }

    // 1. Save normal data
    public void save(String key, String value) {
        cache.put(key, new LockEntry(value, Long.MAX_VALUE));
        System.out.println("✅ Saved to Fake-Redis: " + key);
    }

    // 2. Save data that automatically expires (Our 10-minute lock!)
    public void saveWithExpiration(String key, String value, long timeoutInMinutes) {
        long expireAt = System.currentTimeMillis() + (timeoutInMinutes * 60 * 1000);
        cache.put(key, new LockEntry(value, expireAt));
        System.out.println("✅ Saved to Fake-Redis with " + timeoutInMinutes + " min TTL: " + key);
    }

    // 3. Retrieve data
    public String get(String key) {
        LockEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        
        // If the 10 minutes have passed, automatically delete it and return null
        if (System.currentTimeMillis() > entry.expirationTimeMs) {
            cache.remove(key);
            System.out.println("⏰ Fake-Redis Lock Expired: " + key);
            return null;
        }
        
        return entry.value;
    }

    // 4. Delete data manually
    public void delete(String key) {
        cache.remove(key);
        System.out.println("🗑️ Deleted from Fake-Redis: " + key);
    }
}