package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.view.TarefaViewDto;
import br.com.rennataarruda.todolist.entity.view.TarefaView;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TarefaViewMapperTest {

    private final TarefaViewMapper mapper = new TarefaViewMapper();

    @Test
    void shouldReturnTempoEstimadoAndTempoGastoInMinutes() {
        LocalDateTime dataInicio = LocalDateTime.of(2026, 4, 25, 9, 30);
        TarefaView tarefa = new TarefaView();
        ReflectionTestUtils.setField(tarefa, "dataInicio", dataInicio);
        ReflectionTestUtils.setField(tarefa, "dataFim", dataInicio.plusMinutes(45));
        ReflectionTestUtils.setField(tarefa, "dataConclusao", dataInicio.plusMinutes(30));

        TarefaViewDto dto = mapper.toDto(tarefa);

        assertThat(dto.tempoEstimado()).isEqualTo(45L);
        assertThat(dto.tempoGasto()).isEqualTo(30L);
    }

    @Test
    void shouldReturnNullTempoWhenDatesAreMissing() {
        TarefaViewDto dto = mapper.toDto(new TarefaView());

        assertThat(dto.tempoEstimado()).isNull();
        assertThat(dto.tempoGasto()).isNull();
    }
}
