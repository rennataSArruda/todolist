package br.com.rennataarruda.todolist.dto.filter;

public record EmailConfigSearchFilter(
        String host,
        String username,
        String fromAddress,
        Boolean ativo
) {
}