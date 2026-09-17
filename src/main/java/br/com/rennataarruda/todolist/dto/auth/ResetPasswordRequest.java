package br.com.rennataarruda.todolist.dto.auth;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}