package br.com.rennataarruda.todolist.controller;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {
    public ApiErrorResponse(int status, String message, String path) {
        this(status, message, path, LocalDateTime.now());
    }
}
