package br.com.rennataarruda.todolist.dto.filter;

public record UsuarioSearchFilter(
        String username,
        String email,
        String name,
        String quickSearch,
        Boolean ativo
) {
}