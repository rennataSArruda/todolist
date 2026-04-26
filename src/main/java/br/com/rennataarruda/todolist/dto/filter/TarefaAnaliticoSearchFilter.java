package br.com.rennataarruda.todolist.dto.filter;

import java.time.LocalDateTime;

public record TarefaAnaliticoSearchFilter(
        Long categoriaId,
        Long statusId,
        Long prioridadeId,
        String categoriaNome,
        String statusCodigo,
        String prioridadeCodigo,
        Boolean ativo,
        LocalDateTime dataFimDe,
        LocalDateTime dataFimAte,
        LocalDateTime dataConclusaoDe,
        LocalDateTime dataConclusaoAte,
        LocalDateTime createdAtDe,
        LocalDateTime createdAtAte
) {
}
