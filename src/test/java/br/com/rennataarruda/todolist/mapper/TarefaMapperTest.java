package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.TarefaDto;
import br.com.rennataarruda.todolist.entity.Tarefa;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TarefaMapperTest {

    private final TarefaMapper mapper = new TarefaMapper();

    @Test
    void shouldReturnTempoEstimadoAndTempoGastoInMinutes() {
        LocalDateTime dataInicio = LocalDateTime.of(2026, 4, 25, 8, 0);
        Tarefa tarefa = new Tarefa(
                1L,
                2L,
                3L,
                "Tarefa",
                "Descricao",
                dataInicio,
                dataInicio.plusHours(2),
                dataInicio.plusMinutes(75),
                1L
        );

        TarefaDto dto = mapper.toDto(tarefa);

        assertThat(dto.tempoEstimado()).isEqualTo(120L);
        assertThat(dto.tempoGasto()).isEqualTo(75L);
    }

    @Test
    void shouldReturnNullTempoWhenDatesAreMissing() {
        Tarefa tarefa = new Tarefa(1L, 2L, 3L, "Tarefa", "Descricao", null, null, null, 1L);

        TarefaDto dto = mapper.toDto(tarefa);

        assertThat(dto.tempoEstimado()).isNull();
        assertThat(dto.tempoGasto()).isNull();
    }
}
