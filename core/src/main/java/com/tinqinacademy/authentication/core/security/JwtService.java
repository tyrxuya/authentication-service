package com.tinqinacademy.authentication.core.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;

    public Long getExpiration() {
        return JWT_EXPIRATION;
    }

    public String generateToken(Map<String, String> claims) {
        Date now = Date.from(Instant.now());

        Date expiration = Date.from(
                LocalDateTime.now()
                        .plusMinutes(JWT_EXPIRATION)
                        .toInstant(ZoneOffset.UTC)
        );

        return Jwts.builder()
//                .claim("userId", "")
//                .claim("authorities", "")
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key())
                .compact();
    }

    public boolean isValid(String token) {
        return getExpiration(token).after(Date.from(Instant.now()));
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        String username = claims.getSubject();

        return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    private Date getExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
}
