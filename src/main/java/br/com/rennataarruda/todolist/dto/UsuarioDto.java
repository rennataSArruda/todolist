package br.com.rennataarruda.todolist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UsuarioDto(
        Long id,
        String username,
        String name,
        Boolean ativo,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password
) {
    public UsuarioDto(Long id, String username, String name, String password) {
        this(id, username, name, null, password);
    }
}
