package br.com.rennataarruda.todolist.dto.filter;

public record TarefaCategoriaSearchFilter(
        String nome,
        String descricao,
        String quickSearch,
        Boolean ativo
) {
}
