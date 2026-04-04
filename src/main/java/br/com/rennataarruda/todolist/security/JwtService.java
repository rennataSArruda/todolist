package br.com.rennataarruda.todolist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(String username) {
        return generateToken(username, null);
    }

    public String generateToken(String username, String sessionId) {
        Instant now = Instant.now();

        var builder = Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.expirationMinutes(), ChronoUnit.MINUTES)))
                .signWith(secretKey());

        if (sessionId != null) {
            builder.claim("sid", sessionId);
        }

        return builder.compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractSessionId(String token) {
        return parseClaims(token).get("sid", String.class);
    }

    public LocalDateTime extractExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
