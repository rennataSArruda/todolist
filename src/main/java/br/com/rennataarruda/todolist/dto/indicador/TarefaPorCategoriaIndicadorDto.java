package br.com.rennataarruda.todolist.dto.indicador;

public record TarefaPorCategoriaIndicadorDto(
        Long categoriaId,
        String categoriaNome,
        Long total
) {
}
