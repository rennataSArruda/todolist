package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.view.TarefaAnaliticoResumoDto;
import br.com.rennataarruda.todolist.security.authorization.PodeVisualizarTarefa;
import br.com.rennataarruda.todolist.service.TarefaAnaliticoConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tarefa-analitico")
public class TarefaAnaliticoResource {

    private final TarefaAnaliticoConsultaService service;

    public TarefaAnaliticoResource(TarefaAnaliticoConsultaService service) {
        this.service = service;
    }

    @GetMapping("/semana")
    @PodeVisualizarTarefa
    public ResponseEntity<List<TarefaAnaliticoResumoDto>> tarefasDaSemana() {
        return ResponseEntity.ok(service.tarefasDaSemana());
    }

    @GetMapping("/atrasadas")
    @PodeVisualizarTarefa
    public ResponseEntity<List<TarefaAnaliticoResumoDto>> tarefasAtrasadas() {
        return ResponseEntity.ok(service.tarefasAtrasadas());
    }

    @GetMapping("/vencem-hoje")
    @PodeVisualizarTarefa
    public ResponseEntity<List<TarefaAnaliticoResumoDto>> tarefasQueVencemHojeENaoConcluidas() {
        return ResponseEntity.ok(service.tarefasQueVencemHojeENaoConcluidas());
    }
}
