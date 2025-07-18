package com.expenso.Expenso.service.redis;

import com.expenso.Expenso.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TempUserStore {

  private final RedisTemplate<String, Object> redisTemplate;

  public String cacheUserRequest(RegisterRequest request) {
    String otp = String.format("%06d", new Random().nextInt(999999));
    String keyPrefix = "REG::" + request.getEmail();

    redisTemplate.opsForValue().set(keyPrefix + ":data", request, Duration.ofMinutes(5));
    redisTemplate.opsForValue().set(keyPrefix + ":otp", otp, Duration.ofMinutes(5));

    return otp;
  }

  public RegisterRequest getRequest(String email) {
    String key = "REG::" + email + ":data";
    return (RegisterRequest) redisTemplate.opsForValue().get(key);
  }

  public boolean verifyOtp(String email, String otp) {
    String key = "REG::" + email + ":otp";
    String actualOtp = (String) redisTemplate.opsForValue().get(key);
    return actualOtp != null && actualOtp.equals(otp);
  }

  public void clear(String email) {
    redisTemplate.delete("REG::" + email + ":data");
    redisTemplate.delete("REG::" + email + ":otp");
  }

  public String getOtp(String email) {
    return (String) redisTemplate.opsForValue().get("REG::" + email + ":otp");
  }
}