package br.com.rennataarruda.todolist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final String SESSION_ID_CLAIM = "sid";
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String AUDIENCE_CLAIM = "aud";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(String username, String sessionId) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(sessionId)) {
            throw new IllegalArgumentException("Username e sessionId sao obrigatorios para gerar access token");
        }

        Instant now = Instant.now();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.expirationMinutes(), ChronoUnit.MINUTES)))
                .claim(SESSION_ID_CLAIM, sessionId)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .signWith(secretKey());

        if (StringUtils.hasText(jwtProperties.issuer())) {
            builder.issuer(jwtProperties.issuer());
        }

        if (StringUtils.hasText(jwtProperties.audience())) {
            builder.claim(AUDIENCE_CLAIM, jwtProperties.audience());
        }

        return builder.compact();
    }

    public AccessTokenClaims parseAccessToken(String token) {
        Claims claims = parseClaims(token);

        String tokenId = requireText(claims.getId(), "jti");
        String username = requireText(claims.getSubject(), "sub");
        String sessionId = requireText(claims.get(SESSION_ID_CLAIM, String.class), SESSION_ID_CLAIM);
        String tokenType = requireText(claims.get(TOKEN_TYPE_CLAIM, String.class), TOKEN_TYPE_CLAIM);

        if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
            throw new JwtException("Tipo de token invalido");
        }

        validateExpectedClaim("iss", jwtProperties.issuer(), claims.getIssuer());
        validateAudienceClaim(claims.get(AUDIENCE_CLAIM));

        return new AccessTokenClaims(
                tokenId,
                username,
                sessionId,
                LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault())
        );
    }

    private Claims parseClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JwtException("Token nao informado");
        }

        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void validateExpectedClaim(String claimName, String expectedValue, String actualValue) {
        if (!StringUtils.hasText(expectedValue)) {
            return;
        }

        if (!expectedValue.equals(actualValue)) {
            throw new JwtException("Claim " + claimName + " invalida");
        }
    }

    private void validateAudienceClaim(Object audienceClaim) {
        if (!StringUtils.hasText(jwtProperties.audience())) {
            return;
        }

        if (audienceClaim instanceof String audience && jwtProperties.audience().equals(audience)) {
            return;
        }

        if (audienceClaim instanceof Iterable<?> audiences) {
            for (Object audience : audiences) {
                if (jwtProperties.audience().equals(String.valueOf(audience))) {
                    return;
                }
            }
        }

        throw new JwtException("Claim aud invalida");
    }

    private String requireText(String value, String claimName) {
        if (!StringUtils.hasText(value)) {
            throw new JwtException("Claim " + claimName + " obrigatoria");
        }

        return value;
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public record AccessTokenClaims(
            String tokenId,
            String username,
            String sessionId,
            LocalDateTime expiresAt
    ) {
    }
}

