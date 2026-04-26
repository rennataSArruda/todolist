package br.com.rennataarruda.todolist.entity.fixed.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TarefaPrioridadeEnum {
    BAIXA(1L, "BAIXA", "Baixa", 1),
    MEDIA(2L, "MEDIA", "Media", 2),
    ALTA(3L, "ALTA", "Alta", 3),
    URGENTE(4L, "URGENTE", "Urgente", 4);

    private final Long id;
    private final String codigo;
    private final String descricao;
    private final Integer ordem;

    public static TarefaPrioridadeEnum fromId(Long id) {
        for (TarefaPrioridadeEnum prioridade : values()) {
            if (prioridade.getId().equals(id)) {
                return prioridade;
            }
        }
        return null;
    }
}
