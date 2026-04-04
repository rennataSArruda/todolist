package br.com.rennataarruda.todolist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UsuarioDto(
        Long id,
        String username,
        String name,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password
) {
}
