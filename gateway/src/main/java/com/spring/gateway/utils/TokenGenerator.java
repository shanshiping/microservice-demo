package com.spring.gateway.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TokenGenerator {
    public static void main(String[] args) {
        String secret = "your-secret-key-must-be-at-least-256-bits-long!!";
        String token = Jwts.builder()
                .setSubject("1001")  // 用户ID
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // 1小时后过期
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
        System.out.println(token);
    }
}