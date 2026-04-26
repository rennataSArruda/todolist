package br.com.rennataarruda.todolist.dto.fixed;

public record TarefaPrioridadeDto(
        Long id,
        String codigo,
        String descricao,
        Long ordem,
        Boolean ativo
) {
}
