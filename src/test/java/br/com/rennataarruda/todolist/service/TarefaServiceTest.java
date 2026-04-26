package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.TarefaDto;
import br.com.rennataarruda.todolist.entity.Tarefa;
import br.com.rennataarruda.todolist.mapper.TarefaMapper;
import br.com.rennataarruda.todolist.repository.TarefaRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository repository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private final TarefaMapper mapper = new TarefaMapper();

    @Test
    void shouldRejectCreateWhenTituloIsMissing() {
        TarefaService service = newService();

        assertThatThrownBy(() -> service.create(dto(null, 1L, 1L, true, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Titulo e obrigatorio");
    }

    @Test
    void shouldRejectCreateWhenStatusIsMissing() {
        TarefaService service = newService();

        assertThatThrownBy(() -> service.create(dto("Comprar pao", null, 1L, true, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Status e obrigatorio");
    }

    @Test
    void shouldSetAuthenticatedUserIdOnCreateIgnoringDtoUsuarioIdAndAtivo() {
        TarefaService service = newService();
        when(authenticatedUserProvider.currentUserId()).thenReturn(10L);
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaDto dto = service.create(new TarefaDto(
                null,
                999L,
                2L,
                1L,
                3L,
                "Comprar pao",
                "Descricao",
                LocalDateTime.of(2026, 4, 25, 8, 0),
                null,
                null,
                1L,
                true,
                false,
                null,
                null
        ));

        assertThat(dto.usuarioId()).isEqualTo(10L);
        assertThat(dto.titulo()).isEqualTo("Comprar pao");
        assertThat(dto.importante()).isTrue();
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    void shouldKeepAuthenticatedUserIdAndAtivoOnUpdateIgnoringDtoValues() {
        TarefaService service = newService();
        Tarefa entity = new Tarefa(2L, 1L, 3L, "Antiga", "Descricao", null, null, null, 1L, false);
        entity.definirUsuarioId(10L);

        when(authenticatedUserProvider.currentUserId()).thenReturn(10L);
        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<Tarefa>>any()))
                .thenReturn(Optional.of(entity));
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaDto dto = service.update(1L, new TarefaDto(
                1L,
                999L,
                null,
                2L,
                4L,
                "Atualizada",
                "Nova descricao",
                null,
                null,
                null,
                2L,
                true,
                false,
                null,
                null
        ));

        assertThat(dto.usuarioId()).isEqualTo(10L);
        assertThat(dto.titulo()).isEqualTo("Atualizada");
        assertThat(dto.prioridadeId()).isEqualTo(4L);
        assertThat(dto.importante()).isTrue();
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    void shouldToggleAtivoWhenBloquear() {
        TarefaService service = newService();
        Tarefa entity = new Tarefa(2L, 1L, 3L, "Tarefa", "Descricao", null, null, null, 1L, false);
        entity.definirUsuarioId(10L);

        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<Tarefa>>any()))
                .thenReturn(Optional.of(entity));
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaDto dto = service.bloquear(1L);

        assertThat(dto.ativo()).isFalse();
        verify(repository).save(entity);
    }

    private TarefaDto dto(String titulo, Long statusId, Long prioridadeId, Boolean ativo, Boolean importante) {
        return new TarefaDto(null, null, null, statusId, prioridadeId, titulo, null, null, null, null, null,
                importante, ativo, null, null);
    }

    private TarefaService newService() {
        return new TarefaService(repository, authenticatedUserProvider, mapper);
    }
}
