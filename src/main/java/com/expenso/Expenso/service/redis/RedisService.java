package com.expenso.Expenso.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service for managing Redis cache operations such as saving, retrieving,
 * and deleting key-value pairs with optional TTL (time-to-live).
 */
@Service
public class RedisService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  /**
   * Saves a value in Redis with a given TTL (in seconds).
   */
  public void saveWithTTL(String key, Object value, long ttlSeconds) {
    redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
  }

  /**
   * Retrieves a value from Redis by key.
   */
  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  /**
   * Deletes a key from Redis.
   */
  public void delete(String key) {
    redisTemplate.delete(key);
  }

  /**
   * Checks if a given key exists in Redis.
   */
  public boolean exists(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
}