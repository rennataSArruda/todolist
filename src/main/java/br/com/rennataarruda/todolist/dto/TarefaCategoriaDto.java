package br.com.rennataarruda.todolist.dto;

import java.time.LocalDateTime;

public record TarefaCategoriaDto(
        Long id,
        Long usuarioId,
        String nome,
        String descricao,
        String corHex,
        String icone,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
