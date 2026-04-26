package br.com.rennataarruda.todolist.dto.fixed;

public record TarefaStatusDto(
        Long id,
        String codigo,
        String descricao,
        Boolean ativo
) {
}
