package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.view.TarefaAnaliticoResumoDto;
import br.com.rennataarruda.todolist.repository.view.TarefaAnaliticoViewRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaAnaliticoConsultaService {

    private final TarefaAnaliticoViewRepository repository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public TarefaAnaliticoConsultaService(
            TarefaAnaliticoViewRepository repository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.repository = repository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Transactional(readOnly = true)
    public List<TarefaAnaliticoResumoDto> tarefasDaSemana() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemanaData = hoje.with(DayOfWeek.MONDAY);
        LocalDateTime inicioSemana = inicioSemanaData.atStartOfDay();
        LocalDateTime fimSemana = inicioSemana.plusDays(7);

        return repository.findTarefasAtivasDaSemanaDoUsuarioId(
                authenticatedUserProvider.currentUserId(),
                inicioSemana,
                fimSemana
        );
    }

    @Transactional(readOnly = true)
    public List<TarefaAnaliticoResumoDto> tarefasAtrasadas() {
        return repository.findTarefasAtrasadasAtivasDoUsuarioId(
                authenticatedUserProvider.currentUserId(),
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public List<TarefaAnaliticoResumoDto> tarefasQueVencemHojeENaoConcluidas() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1);

        return repository.findTarefasQueVencemHojeENaoConcluidasDoUsuarioId(
                authenticatedUserProvider.currentUserId(),
                inicioDia,
                fimDia
        );
    }
}
