package com.example.payment.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 1. Save normal data
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        System.out.println("✅ Saved to Redis: " + key);
    }

    // 2. Save data that automatically deletes itself after X minutes
    public void saveWithExpiration(String key, String value, long timeoutInMinutes) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(timeoutInMinutes));
        System.out.println("✅ Saved to Redis with " + timeoutInMinutes + " min TTL: " + key);
    }

    // 3. Retrieve data
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 4. Delete data manually
    public void delete(String key) {
        redisTemplate.delete(key);
        System.out.println("🗑️ Deleted from Redis: " + key);
    }
}