package br.com.rennataarruda.todolist.dto.auth;

public record FirstAccessRequest(
        String username,
        String email,
        String name,
        String password
) {
}
