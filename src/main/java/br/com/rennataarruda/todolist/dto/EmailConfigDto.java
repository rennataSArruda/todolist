package br.com.rennataarruda.todolist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record EmailConfigDto(
        Long id,
        String host,
        Integer port,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String fromAddress,
        String fromName,
        Boolean auth,
        Boolean startTls,
        Boolean ssl,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}