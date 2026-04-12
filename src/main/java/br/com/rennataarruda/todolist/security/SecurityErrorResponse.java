package br.com.rennataarruda.todolist.security;

import java.time.LocalDateTime;

public record SecurityErrorResponse(
        int status,
        String code,
        SecurityErrorAction action,
        String message,
        String path,
        LocalDateTime timestamp
) {
    public SecurityErrorResponse(int status, String code, SecurityErrorAction action, String message, String path) {
        this(status, code, action, message, path, LocalDateTime.now());
    }
}
