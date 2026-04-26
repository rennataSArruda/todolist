package br.com.rennataarruda.todolist.dto.view;

import java.time.LocalDateTime;

public record TarefaViewDto(
        Long id,
        Long usuarioId,
        Long categoriaId,
        String categoriaNome,
        String categoriaDescricao,
        Long statusId,
        String statusCodigo,
        String statusDescricao,
        Long prioridadeId,
        String prioridadeCodigo,
        String prioridadeDescricao,
        Long prioridadeOrdem,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        LocalDateTime dataConclusao,
        Long tempoEstimado,
        Long tempoGasto,
        Long posicao,
        Boolean importante,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
