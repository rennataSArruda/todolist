package br.com.rennataarruda.todolist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";

    private final JwtProperties jwtProperties = new JwtProperties(
            SECRET,
            15,
            7,
            1,
            "todolist-api",
            "todolist-api"
    );

    private final JwtService service = new JwtService(jwtProperties);

    @Test
    void shouldGenerateAccessTokenWithExpectedClaims() {
        String token = service.generateAccessToken("admin", "session-1");

        JwtService.AccessTokenClaims parsed = service.parseAccessToken(token);
        Claims rawClaims = Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(parsed.username()).isEqualTo("admin");
        assertThat(parsed.sessionId()).isEqualTo("session-1");
        assertThat(parsed.tokenId()).isNotBlank();
        assertThat(parsed.expiresAt()).isAfter(LocalDateTime.now());
        assertThat(rawClaims.getId()).isEqualTo(parsed.tokenId());
        assertThat(rawClaims.getIssuer()).isEqualTo("todolist-api");
        assertThat(String.valueOf(rawClaims.get("aud"))).contains("todolist-api");
        assertThat(rawClaims.get("token_type", String.class)).isEqualTo("access");
        assertThat(rawClaims.get("sid", String.class)).isEqualTo("session-1");
    }

    @Test
    void shouldRejectTokenWithUnexpectedType() {
        String token = Jwts.builder()
                .subject("admin")
                .id("jti-1")
                .issuer("todolist-api")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .claim("aud", "todolist-api")
                .claim("sid", "session-1")
                .claim("token_type", "refresh")
                .signWith(secretKey())
                .compact();

        assertThatThrownBy(() -> service.parseAccessToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Tipo de token invalido");
    }

    @Test
    void shouldRejectTokenWithUnexpectedIssuer() {
        String token = Jwts.builder()
                .subject("admin")
                .id("jti-1")
                .issuer("other-issuer")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .claim("aud", "todolist-api")
                .claim("sid", "session-1")
                .claim("token_type", "access")
                .signWith(secretKey())
                .compact();

        assertThatThrownBy(() -> service.parseAccessToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Claim iss invalida");
    }

    @Test
    void shouldRejectTokenWithUnexpectedAudience() {
        String token = Jwts.builder()
                .subject("admin")
                .id("jti-1")
                .issuer("todolist-api")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .claim("aud", "other-audience")
                .claim("sid", "session-1")
                .claim("token_type", "access")
                .signWith(secretKey())
                .compact();

        assertThatThrownBy(() -> service.parseAccessToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Claim aud invalida");
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}


