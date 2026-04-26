package br.com.rennataarruda.todolist.dto.filter;

import java.time.LocalDateTime;

public record TarefaSearchFilter(
        Long categoriaId,
        Long statusId,
        Long prioridadeId,
        String titulo,
        String descricao,
        String categoriaNome,
        String statusCodigo,
        String prioridadeCodigo,
        Boolean importante,
        Boolean ativo,
        LocalDateTime dataInicioDe,
        LocalDateTime dataInicioAte,
        LocalDateTime dataFimDe,
        LocalDateTime dataFimAte,
        LocalDateTime dataConclusaoDe,
        LocalDateTime dataConclusaoAte
) {
}
