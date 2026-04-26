package br.com.rennataarruda.todolist.dto;

import java.time.LocalDateTime;

public record TarefaDto(
        Long id,
        Long usuarioId,
        Long categoriaId,
        Long statusId,
        Long prioridadeId,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        LocalDateTime dataConclusao,
        Long tempoEstimado,
        Long tempoGasto,
        Long posicao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
