package br.com.rennataarruda.todolist.dto.auth;

import java.util.List;

public record AuthenticatedUserResponse(
        Long id,
        String username,
        String nome,
        Perfil perfil,
        List<String> authorities,
        boolean root
) {
    public record Perfil(
            Long id,
            String codigo
    ) {
    }
}
