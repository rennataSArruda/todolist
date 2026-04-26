package br.com.rennataarruda.todolist.repository.view;

import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorStatusIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorPrioridadeIndicadorDto;

import java.util.List;

public interface TarefaAnaliticoViewRepositoryCustom {

    List<TarefaPorStatusIndicadorDto> countTarefasAtivasPorStatusDoUsuarioId(Long usuarioId);

    List<TarefaPorPrioridadeIndicadorDto> countTarefasAtivasPorPrioridadeDoUsuarioId(Long usuarioId);

    List<TarefaPorCategoriaIndicadorDto> countTarefasAtivasPorCategoriaDoUsuarioId(Long usuarioId);

    List<TarefaPorCategoriaPrioridadeIndicadorDto> countTarefasAtivasPorCategoriaEPrioridadeDoUsuarioId(Long usuarioId);
}
