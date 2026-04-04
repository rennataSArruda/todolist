package br.com.rennataarruda.todolist.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
