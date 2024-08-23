package com.tinqinacademy.authentication.core.security;

import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.BlacklistedTokenRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public String generateToken(Map<String, String> claims) {
        Date now = Date.from(Instant.now());

        Date expiration = Date.from(
                LocalDateTime.now()
                        .plusMinutes(JWT_EXPIRATION)
                        .toInstant(ZoneOffset.UTC)
        );

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key())
                .compact();
    }

    public boolean isValid(String token) {
        return getExpiration(token).after(Date.from(Instant.now())) &&
                blacklistedTokenRepository.findById(token).isEmpty();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(String token) {
        Claims claims = parseToken(token);
        String userId = claims.get("userId").toString();

        return userId;
    }

    public Date getExpiration(String token) {
        Claims claims = parseToken(token);

        return claims.getExpiration();
    }

    public String getUsername(String token) {
        Claims claims = parseToken(token);

        return claims.get("username").toString();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }
}
