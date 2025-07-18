package com.expenso.Expenso.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  public String generateToken(String email) {
    return Jwts.builder()
               .setSubject(email)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
               .signWith(SignatureAlgorithm.HS256, jwtSecret)
               .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    Date expiration = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
    return expiration.before(new Date());
  }
}