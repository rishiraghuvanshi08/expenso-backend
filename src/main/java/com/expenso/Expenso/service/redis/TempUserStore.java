package com.expenso.Expenso.service.redis;

import com.expenso.Expenso.dto.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

/**
 * Temporary store for handling user registration data and OTP verification using Redis.
 */
@Service
@RequiredArgsConstructor
public class TempUserStore {

  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * Caches the user registration request and OTP for 5 minutes.
   */
  public String cacheUserRequest(RegisterRequest request) {
    String otp = String.format("%06d", new Random().nextInt(999999));
    String keyPrefix = "REG::" + request.getEmail();

    redisTemplate.opsForValue().set(keyPrefix + ":data", request, Duration.ofMinutes(5));
    redisTemplate.opsForValue().set(keyPrefix + ":otp", otp, Duration.ofMinutes(5));

    return otp;
  }

  /**
   * Retrieves the cached registration request for the given email.
   */
  public RegisterRequest getRequest(String email) {
    String key = "REG::" + email + ":data";
    return (RegisterRequest) redisTemplate.opsForValue().get(key);
  }

  /**
   * Verifies if the provided OTP matches the stored one for the given email.
   */
  public boolean verifyOtp(String email, String otp) {
    String key = "REG::" + email + ":otp";
    String actualOtp = (String) redisTemplate.opsForValue().get(key);
    return actualOtp != null && actualOtp.equals(otp);
  }

  /**
   * Clears cached registration data and OTP for the given email.
   */
  public void clear(String email) {
    redisTemplate.delete("REG::" + email + ":data");
    redisTemplate.delete("REG::" + email + ":otp");
  }

  /**
   * Retrieves the cached OTP for the given email.
   */
  public String getOtp(String email) {
    return (String) redisTemplate.opsForValue().get("REG::" + email + ":otp");
  }
}