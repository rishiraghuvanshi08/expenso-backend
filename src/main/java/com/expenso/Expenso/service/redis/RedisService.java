package com.expenso.Expenso.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  public void saveWithTTL(String key, Object value, long ttlSeconds) {
    redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  public boolean exists(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
}