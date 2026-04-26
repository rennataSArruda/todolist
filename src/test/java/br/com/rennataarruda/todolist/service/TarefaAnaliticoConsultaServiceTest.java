package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.repository.view.TarefaAnaliticoViewRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaAnaliticoConsultaServiceTest {

    @Mock
    private TarefaAnaliticoViewRepository repository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Test
    void shouldSearchCurrentWeekTasksForCurrentUser() {
        TarefaAnaliticoConsultaService service = new TarefaAnaliticoConsultaService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(8L);
        when(repository.findTarefasAtivasDaSemanaDoUsuarioId(eq(8L), any(), any()))
                .thenReturn(List.of());

        service.tarefasDaSemana();

        ArgumentCaptor<LocalDateTime> inicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fimCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(repository).findTarefasAtivasDaSemanaDoUsuarioId(eq(8L), inicioCaptor.capture(), fimCaptor.capture());

        LocalDateTime inicioSemana = inicioCaptor.getValue();
        LocalDateTime fimSemana = fimCaptor.getValue();

        assertEquals(DayOfWeek.MONDAY, inicioSemana.getDayOfWeek());
        assertEquals(0, inicioSemana.getHour());
        assertEquals(0, inicioSemana.getMinute());
        assertEquals(inicioSemana.plusDays(7), fimSemana);
    }

    @Test
    void shouldSearchOverdueTasksForCurrentUser() {
        TarefaAnaliticoConsultaService service = new TarefaAnaliticoConsultaService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(8L);
        when(repository.findTarefasAtrasadasAtivasDoUsuarioId(eq(8L), any())).thenReturn(List.of());

        service.tarefasAtrasadas();

        ArgumentCaptor<LocalDateTime> dataAtualCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(repository).findTarefasAtrasadasAtivasDoUsuarioId(eq(8L), dataAtualCaptor.capture());
        verify(authenticatedUserProvider).currentUserId();
        assertNotNull(dataAtualCaptor.getValue());
    }

    @Test
    void shouldSearchTodayDueUnfinishedTasksForCurrentUser() {
        TarefaAnaliticoConsultaService service = new TarefaAnaliticoConsultaService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(8L);
        when(repository.findTarefasQueVencemHojeENaoConcluidasDoUsuarioId(eq(8L), any(), any())).thenReturn(List.of());

        service.tarefasQueVencemHojeENaoConcluidas();

        ArgumentCaptor<LocalDateTime> inicioDiaCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fimDiaCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(repository).findTarefasQueVencemHojeENaoConcluidasDoUsuarioId(eq(8L), inicioDiaCaptor.capture(), fimDiaCaptor.capture());

        LocalDateTime inicioDia = inicioDiaCaptor.getValue();
        LocalDateTime fimDia = fimDiaCaptor.getValue();

        assertEquals(0, inicioDia.getHour());
        assertEquals(0, inicioDia.getMinute());
        assertEquals(0, inicioDia.getSecond());
        assertEquals(inicioDia.plusDays(1), fimDia);
    }
}
