package br.com.rennataarruda.todolist.dto.indicador;

public record TarefaPorCategoriaPrioridadeIndicadorDto(
        Long categoriaId,
        String categoriaNome,
        Long prioridadeId,
        String prioridadeCodigo,
        String prioridadeDescricao,
        Long total
) {
}
