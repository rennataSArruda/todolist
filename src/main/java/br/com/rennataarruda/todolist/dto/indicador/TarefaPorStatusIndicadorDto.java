package br.com.rennataarruda.todolist.dto.indicador;

public record TarefaPorStatusIndicadorDto(
        Long statusId,
        String statusCodigo,
        String statusDescricao,
        Long total
) {
}
