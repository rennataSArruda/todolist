package br.com.rennataarruda.todolist.security;

public record AuthenticatedUser(
        Long id,
        String username,
        String name,
        boolean root,
        Long perfilId,
        String perfilCodigo,
        String sessionId
) {
}
