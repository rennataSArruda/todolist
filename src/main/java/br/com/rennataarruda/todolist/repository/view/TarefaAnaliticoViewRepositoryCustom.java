package br.com.rennataarruda.todolist.repository.view;

import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorStatusIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.view.TarefaAnaliticoResumoDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TarefaAnaliticoViewRepositoryCustom {

    List<TarefaPorStatusIndicadorDto> countTarefasAtivasPorStatusDoUsuarioId(Long usuarioId);

    List<TarefaPorPrioridadeIndicadorDto> countTarefasAtivasPorPrioridadeDoUsuarioId(Long usuarioId);

    List<TarefaPorCategoriaIndicadorDto> countTarefasAtivasPorCategoriaDoUsuarioId(Long usuarioId);

    List<TarefaPorCategoriaPrioridadeIndicadorDto> countTarefasAtivasPorCategoriaEPrioridadeDoUsuarioId(Long usuarioId);

    List<TarefaAnaliticoResumoDto> findTarefasAtivasDaSemanaDoUsuarioId(
            Long usuarioId,
            LocalDateTime inicioSemana,
            LocalDateTime fimSemana
    );

    List<TarefaAnaliticoResumoDto> findTarefasAtrasadasAtivasDoUsuarioId(
            Long usuarioId,
            LocalDateTime dataAtual
    );

    List<TarefaAnaliticoResumoDto> findTarefasQueVencemHojeENaoConcluidasDoUsuarioId(
            Long usuarioId,
            LocalDateTime inicioDia,
            LocalDateTime fimDia
    );
}
