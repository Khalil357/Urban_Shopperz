package com.urbanshopper.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey accessKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-token-expiration}") long accessExp,
                      @Value("${jwt.refresh-token-expiration}") long refreshExp) {
        this.accessKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExp;
        this.refreshExpiration = refreshExp;
    }

    public String generateAccessToken(UUID userId, String role) {
        return Jwts.builder().subject(userId.toString()).claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .signWith(accessKey).compact();
    }

    public String generateRefreshToken(UUID userId) {
        var key = Keys.hmacShaKeyFor((accessKey.getEncoded() + "-refresh").toString().getBytes());
        return Jwts.builder().subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(key).compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser().verifyWith(accessKey).build().parseSignedClaims(token).getPayload();
    }
}
