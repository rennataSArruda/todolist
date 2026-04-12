package br.com.rennataarruda.todolist.dto.auth;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
