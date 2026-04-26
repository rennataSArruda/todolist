package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorStatusIndicadorDto;
import br.com.rennataarruda.todolist.entity.view.TarefaAnaliticoView;
import br.com.rennataarruda.todolist.repository.view.TarefaAnaliticoViewRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaAnaliticoViewServiceTest {

    @Mock
    private TarefaAnaliticoViewRepository repository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Test
    void shouldSearchUsingUserScopedSpecification() {
        TarefaAnaliticoViewService service = new TarefaAnaliticoViewService(repository, authenticatedUserProvider);
        when(repository.findAll(any(Specification.class))).thenReturn(List.of());
        when(authenticatedUserProvider.currentUserId()).thenReturn(1L);

        service.search(null);

        verify(repository).findAll(any(Specification.class));
        verify(authenticatedUserProvider).currentUserId();
    }

    @Test
    void shouldReturnTotalActiveTasksForCurrentUser() {
        TarefaAnaliticoViewService service = new TarefaAnaliticoViewService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(7L);
        when(repository.count(any(Specification.class))).thenReturn(12L);

        var result = service.totalTarefasAtivasDoUsuarioLogado();

        assertEquals(12L, result.totalTarefas());
        verify(authenticatedUserProvider).currentUserId();
        verify(repository).count(any(Specification.class));
    }

    @Test
    void shouldReturnTotalActiveTasksByStatusForCurrentUser() {
        TarefaAnaliticoViewService service = new TarefaAnaliticoViewService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(7L);
        when(repository.countTarefasAtivasPorStatusDoUsuarioId(7L)).thenReturn(List.of(
                new TarefaPorStatusIndicadorDto(1L, "TODO", "A fazer", 5L)
        ));

        var result = service.totalTarefasAtivasPorStatusDoUsuarioLogado();

        assertEquals(1, result.size());
        assertEquals(5L, result.getFirst().total());
        verify(authenticatedUserProvider).currentUserId();
        verify(repository).countTarefasAtivasPorStatusDoUsuarioId(7L);
    }

    @Test
    void shouldReturnTotalActiveTasksByPriorityForCurrentUser() {
        TarefaAnaliticoViewService service = new TarefaAnaliticoViewService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(7L);
        when(repository.countTarefasAtivasPorPrioridadeDoUsuarioId(7L)).thenReturn(List.of(
                new TarefaPorPrioridadeIndicadorDto(1L, "ALTA", "Alta", 3L)
        ));

        var result = service.totalTarefasAtivasPorPrioridadeDoUsuarioLogado();

        assertEquals(1, result.size());
        assertEquals(3L, result.getFirst().total());
        verify(authenticatedUserProvider).currentUserId();
        verify(repository).countTarefasAtivasPorPrioridadeDoUsuarioId(7L);
    }

    @Test
    void shouldReturnTotalActiveTasksByCategoryForCurrentUser() {
        TarefaAnaliticoViewService service = new TarefaAnaliticoViewService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(7L);
        when(repository.countTarefasAtivasPorCategoriaDoUsuarioId(7L)).thenReturn(List.of(
                new TarefaPorCategoriaIndicadorDto(2L, "Pessoal", 4L)
        ));

        var result = service.totalTarefasAtivasPorCategoriaDoUsuarioLogado();

        assertEquals(1, result.size());
        assertEquals(4L, result.getFirst().total());
        verify(authenticatedUserProvider).currentUserId();
        verify(repository).countTarefasAtivasPorCategoriaDoUsuarioId(7L);
    }

    @Test
    void shouldReturnTotalActiveTasksByCategoryAndPriorityForCurrentUser() {
        TarefaAnaliticoViewService service = new TarefaAnaliticoViewService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(7L);
        when(repository.countTarefasAtivasPorCategoriaEPrioridadeDoUsuarioId(7L)).thenReturn(List.of(
                new TarefaPorCategoriaPrioridadeIndicadorDto(2L, "Pessoal", 1L, "ALTA", "Alta", 2L)
        ));

        var result = service.totalTarefasAtivasPorCategoriaEPrioridadeDoUsuarioLogado();

        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst().total());
        verify(authenticatedUserProvider).currentUserId();
        verify(repository).countTarefasAtivasPorCategoriaEPrioridadeDoUsuarioId(7L);
    }
}
