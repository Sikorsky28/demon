package com.example.demon.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Секретный ключ — должен быть не короче 32 символов (256 бит)
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "your-very-strong-secret-key-that-is-32-bytes!".getBytes()
    );

    public SecretKey getSecretKey() {
        return secretKey;
    }

    // Генерация access-токена (например, срок жизни 1 час)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 час
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Генерация refresh-токена (например, срок жизни 7 дней)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 дней
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Извлечение имени пользователя (subject) из токена
    public String extractUsername(String token) {
        try {
            return getUsernameFromToken(token);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Невалидный токен", e);
        }
    }

    // Извлечение subject (имени пользователя)
    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    // Проверка действительности токена (подпись и срок годности)
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Проверка истек ли токен
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Извлечение всех claims
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
