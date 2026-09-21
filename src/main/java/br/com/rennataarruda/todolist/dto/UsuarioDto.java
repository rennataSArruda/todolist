package br.com.rennataarruda.todolist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UsuarioDto(
        Long id,
        String username,
        String email,
        String name,
        Boolean ativo,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
        Long perfilId
) {
    public UsuarioDto(Long id, String username, String email, String name, Boolean ativo, String password) {
        this(id, username, email, name, ativo, password, null);
    }

    public UsuarioDto(Long id, String username, String name, Boolean ativo, String password) {
        this(id, username, null, name, ativo, password, null);
    }

    public UsuarioDto(Long id, String username, String name, String password) {
        this(id, username, null, name, null, password, null);
    }
}