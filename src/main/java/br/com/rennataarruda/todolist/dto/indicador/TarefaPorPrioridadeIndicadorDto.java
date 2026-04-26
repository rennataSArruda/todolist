package br.com.rennataarruda.todolist.dto.indicador;

public record TarefaPorPrioridadeIndicadorDto(
        Long prioridadeId,
        String prioridadeCodigo,
        String prioridadeDescricao,
        Long total
) {
}
