package br.com.rennataarruda.todolist.entity.fixed.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TarefaStatusEnum {
    PENDENTE(1L, "PENDENTE", "Pendente"),
    EM_ANDAMENTO(2L, "EM_ANDAMENTO", "Em andamento"),
    CONCLUIDA(3L, "CONCLUIDA", "Concluida"),
    CANCELADA(4L, "CANCELADA", "Cancelada");

    private final Long id;
    private final String codigo;
    private final String descricao;

    public static TarefaStatusEnum fromId(Long id) {
        for (TarefaStatusEnum status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }
}
