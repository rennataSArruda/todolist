package br.com.rennataarruda.todolist.security;

import org.springframework.http.HttpStatus;

public enum SecurityErrorCatalog {
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_MISSING", "Token nao informado", SecurityErrorAction.LOGIN),
    TOKEN_INVALIDATED(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_INVALIDATED", "Token invalidado", SecurityErrorAction.LOGIN),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_EXPIRED", "Token expirado", SecurityErrorAction.TOKEN_REFRESH),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_INVALID", "Token invalido", SecurityErrorAction.LOGIN),
    SESSION_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_SESSION_INVALID", "Sessao invalida ou revogada", SecurityErrorAction.LOGIN),
    USER_WITHOUT_PROFILE(HttpStatus.FORBIDDEN, "AUTH_USER_WITHOUT_PROFILE", "Usuario sem perfil de acesso", SecurityErrorAction.FORBIDDEN),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_ACCESS_DENIED", "Voce nao possui permissao para acessar este recurso.", SecurityErrorAction.FORBIDDEN),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Acesso nao autorizado. Por favor, forneca credenciais validas.", SecurityErrorAction.LOGIN);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final SecurityErrorAction action;

    SecurityErrorCatalog(HttpStatus status, String code, String message, SecurityErrorAction action) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.action = action;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public SecurityErrorAction action() {
        return action;
    }
}
