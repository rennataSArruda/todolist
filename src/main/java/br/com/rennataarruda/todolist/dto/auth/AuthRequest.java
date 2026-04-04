package br.com.rennataarruda.todolist.dto.auth;

public record AuthRequest(
        String username,
        String password
) {
}
