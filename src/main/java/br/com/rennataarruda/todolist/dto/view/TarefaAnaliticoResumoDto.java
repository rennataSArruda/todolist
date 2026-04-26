package br.com.rennataarruda.todolist.dto.view;

import java.time.LocalDateTime;

public record TarefaAnaliticoResumoDto(
        Long tarefaId,
        String titulo,
        String statusDescricao,
        String prioridadeDescricao,
        String categoriaNome,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        LocalDateTime dataConclusao
) {
}
