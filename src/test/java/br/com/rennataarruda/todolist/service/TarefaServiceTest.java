package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.TarefaDto;
import br.com.rennataarruda.todolist.entity.Tarefa;
import br.com.rennataarruda.todolist.mapper.TarefaMapper;
import br.com.rennataarruda.todolist.repository.TarefaCategoriaRepository;
import br.com.rennataarruda.todolist.repository.TarefaRepository;
import br.com.rennataarruda.todolist.repository.fixed.TarefaPrioridadeRepository;
import br.com.rennataarruda.todolist.repository.fixed.TarefaStatusRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import br.com.rennataarruda.todolist.entity.fixed.enumerations.TarefaPrioridadeEnum;
import br.com.rennataarruda.todolist.entity.fixed.enumerations.TarefaStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository repository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private TarefaStatusRepository statusRepository;

    @Mock
    private TarefaPrioridadeRepository prioridadeRepository;

    @Mock
    private TarefaCategoriaRepository categoriaRepository;

    private final TarefaMapper mapper = new TarefaMapper();

    @Test
    void shouldRejectCreateWhenTituloIsMissing() {
        TarefaService service = newService();

        assertThatThrownBy(() -> service.create(dto(null, TarefaStatusEnum.PENDENTE.getId(), TarefaPrioridadeEnum.BAIXA.getId(), true, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Titulo e obrigatorio");
    }

    @Test
    void shouldRejectCreateWhenStatusIsMissing() {
        TarefaService service = newService();

        assertThatThrownBy(() -> service.create(dto("Comprar pao", null, TarefaPrioridadeEnum.BAIXA.getId(), true, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Status e obrigatorio");
    }


    @Test
    void shouldKeepAuthenticatedUserIdAndAtivoOnUpdateIgnoringDtoValues() {
        TarefaService service = newService();
        Tarefa entity = new Tarefa(null, TarefaStatusEnum.PENDENTE.getId(), TarefaPrioridadeEnum.ALTA.getId(), "Antiga", "Descricao", null, null, null, 1L);
        entity.definirUsuarioId(10L);

        when(authenticatedUserProvider.currentUserId()).thenReturn(10L);
        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<Tarefa>>any()))
                .thenReturn(Optional.of(entity));
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Define IDs from enums used in DTO
        Long pendingStatusId = TarefaStatusEnum.PENDENTE.getId();
        Long urgentePrioridadeId = TarefaPrioridadeEnum.URGENTE.getId();

        // Mocking the entities instead of instantiating with new
        br.com.rennataarruda.todolist.entity.fixed.TarefaStatus status = mock(br.com.rennataarruda.todolist.entity.fixed.TarefaStatus.class);
        when(status.getAtivo()).thenReturn(true);
        when(statusRepository.findById(pendingStatusId)).thenReturn(Optional.of(status));

        br.com.rennataarruda.todolist.entity.fixed.TarefaPrioridade prioridade = mock(br.com.rennataarruda.todolist.entity.fixed.TarefaPrioridade.class);
        when(prioridade.getAtivo()).thenReturn(true);
        when(prioridadeRepository.findById(urgentePrioridadeId)).thenReturn(Optional.of(prioridade));

        TarefaDto dto = service.update(1L, new TarefaDto(
                1L,
                999L,
                null,
                TarefaStatusEnum.PENDENTE.getId(),
                TarefaPrioridadeEnum.URGENTE.getId(),
                "Atualizada",
                "Nova descricao",
                null,
                null,
                null,
                null,
                null,
                2L,
                true,
                null,
                null
        ));

        assertThat(dto.usuarioId()).isEqualTo(10L);
        assertThat(dto.titulo()).isEqualTo("Atualizada");
        assertThat(dto.prioridadeId()).isEqualTo(TarefaPrioridadeEnum.URGENTE.getId());
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    void shouldToggleAtivoWhenBloquear() {
        TarefaService service = newService();
        Tarefa entity = new Tarefa(2L, 1L, 3L, "Tarefa", "Descricao", null, null, null, 1L);
        entity.definirUsuarioId(10L);

        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<Tarefa>>any()))
                .thenReturn(Optional.of(entity));
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaDto dto = service.bloquear(1L);

        assertThat(dto.ativo()).isFalse();
        verify(repository).save(entity);
    }

    private TarefaDto dto(String titulo, Long statusId, Long prioridadeId, Boolean ativo, Boolean importante) {
        return new TarefaDto(null, null, null, statusId, prioridadeId, titulo, null, null, null, null, null, null, null, ativo, null, null);
    }

    private TarefaService newService() {
        return new TarefaService(repository, authenticatedUserProvider, mapper, statusRepository, prioridadeRepository, categoriaRepository);
    }
}
