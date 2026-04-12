package br.com.rennataarruda.todolist.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes,
        long refreshExpirationDays,
        int maxSessions,
        String issuer,
        String audience
) {
}
