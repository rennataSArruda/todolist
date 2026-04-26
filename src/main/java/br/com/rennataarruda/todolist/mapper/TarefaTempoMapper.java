package br.com.rennataarruda.todolist.mapper;

import java.time.Duration;
import java.time.LocalDateTime;

final class TarefaTempoMapper {

    private TarefaTempoMapper() {
    }

    static Long calcularEmMinutos(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            return null;
        }
        return Duration.between(inicio, fim).toMinutes();
    }
}
