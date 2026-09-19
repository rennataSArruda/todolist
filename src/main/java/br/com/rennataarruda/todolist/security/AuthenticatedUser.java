package br.com.rennataarruda.todolist.security;

public record AuthenticatedUser(
        Long id,
        String username,
        String name,
        boolean root,
        Long perfilId,
        String perfilCodigo,
        String sessionId,
        String email
) {
    public AuthenticatedUser(
            Long id,
            String username,
            String name,
            boolean root,
            Long perfilId,
            String perfilCodigo,
            String sessionId
    ) {
        this(id, username, name, root, perfilId, perfilCodigo, sessionId, null);
    }
}