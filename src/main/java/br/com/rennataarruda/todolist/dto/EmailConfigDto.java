package br.com.rennataarruda.todolist.dto;

import java.time.LocalDateTime;

public record EmailConfigDto(
        Long id,
        String host,
        Integer port,
        String username,
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
