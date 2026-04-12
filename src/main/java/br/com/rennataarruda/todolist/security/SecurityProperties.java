package br.com.rennataarruda.todolist.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        Cors cors,
        Headers headers
) {
    public record Cors(List<String> allowedOrigins) {}
    public record Headers(boolean hstsEnabled) {}
}
