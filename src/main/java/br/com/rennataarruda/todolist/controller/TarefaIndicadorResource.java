package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorStatusIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaTotalIndicadorDto;
import br.com.rennataarruda.todolist.security.authorization.PodeVisualizarIndicadoresTarefa;
import br.com.rennataarruda.todolist.service.TarefaAnaliticoViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/indicadores/tarefa")
public class TarefaIndicadorResource {

    private final TarefaAnaliticoViewService tarefaAnaliticoViewService;

    public TarefaIndicadorResource(TarefaAnaliticoViewService tarefaAnaliticoViewService) {
        this.tarefaAnaliticoViewService = tarefaAnaliticoViewService;
    }

    @GetMapping("/total-ativas")
    @PodeVisualizarIndicadoresTarefa
    public ResponseEntity<TarefaTotalIndicadorDto> totalTarefasAtivas() {
        return ResponseEntity.ok(tarefaAnaliticoViewService.totalTarefasAtivasDoUsuarioLogado());
    }

    @GetMapping("/por-status")
    @PodeVisualizarIndicadoresTarefa
    public ResponseEntity<List<TarefaPorStatusIndicadorDto>> totalTarefasAtivasPorStatus() {
        return ResponseEntity.ok(tarefaAnaliticoViewService.totalTarefasAtivasPorStatusDoUsuarioLogado());
    }

    @GetMapping("/por-prioridade")
    @PodeVisualizarIndicadoresTarefa
    public ResponseEntity<List<TarefaPorPrioridadeIndicadorDto>> totalTarefasAtivasPorPrioridade() {
        return ResponseEntity.ok(tarefaAnaliticoViewService.totalTarefasAtivasPorPrioridadeDoUsuarioLogado());
    }

    @GetMapping("/por-categoria")
    @PodeVisualizarIndicadoresTarefa
    public ResponseEntity<List<TarefaPorCategoriaIndicadorDto>> totalTarefasAtivasPorCategoria() {
        return ResponseEntity.ok(tarefaAnaliticoViewService.totalTarefasAtivasPorCategoriaDoUsuarioLogado());
    }

    @GetMapping("/por-categoria-prioridade")
    @PodeVisualizarIndicadoresTarefa
    public ResponseEntity<List<TarefaPorCategoriaPrioridadeIndicadorDto>> totalTarefasAtivasPorCategoriaEPrioridade() {
        return ResponseEntity.ok(tarefaAnaliticoViewService.totalTarefasAtivasPorCategoriaEPrioridadeDoUsuarioLogado());
    }
}
