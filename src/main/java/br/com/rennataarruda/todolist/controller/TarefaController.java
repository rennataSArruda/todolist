package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.controller.commons.AbstractCrudController;
import br.com.rennataarruda.todolist.controller.commons.SearchPaginationRequest;
import br.com.rennataarruda.todolist.dto.TarefaDto;
import br.com.rennataarruda.todolist.dto.filter.TarefaSearchFilter;
import br.com.rennataarruda.todolist.dto.view.TarefaViewDto;
import br.com.rennataarruda.todolist.security.authorization.PodeBloquearTarefa;
import br.com.rennataarruda.todolist.security.authorization.PodeCriarTarefa;
import br.com.rennataarruda.todolist.security.authorization.PodeEditarTarefa;
import br.com.rennataarruda.todolist.security.authorization.PodeVisualizarTarefa;
import br.com.rennataarruda.todolist.service.TarefaService;
import br.com.rennataarruda.todolist.service.TarefaViewService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tarefa")
public class TarefaController extends AbstractCrudController<Long, TarefaDto, TarefaService> {

    private final TarefaViewService tarefaViewService;

    public TarefaController(TarefaService service, TarefaViewService tarefaViewService) {
        super(service);
        this.tarefaViewService = tarefaViewService;
    }

    @Override
    @GetMapping
    @PodeVisualizarTarefa
    public ResponseEntity<List<TarefaDto>> get() {
        return super.get();
    }

    @Override
    @GetMapping("/{id}")
    @PodeVisualizarTarefa
    public ResponseEntity<TarefaDto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PostMapping
    @PodeCriarTarefa
    public ResponseEntity<TarefaDto> create(@RequestBody TarefaDto dto) {
        return super.create(dto);
    }

    @Override
    @PutMapping("/{id}")
    @PodeEditarTarefa
    public ResponseEntity<TarefaDto> update(@PathVariable Long id, @RequestBody TarefaDto dto) {
        return super.update(id, dto);
    }

    @PutMapping("/{id}/bloquear")
    @PodeBloquearTarefa
    public ResponseEntity<TarefaDto> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(service().bloquear(id));
    }

    @PostMapping("/search")
    @PodeVisualizarTarefa
    public ResponseEntity<List<TarefaViewDto>> search(@RequestBody TarefaSearchFilter filter) {
        return ResponseEntity.ok(tarefaViewService.search(filter));
    }

    @PostMapping("/search-pagination")
    @PodeVisualizarTarefa
    public ResponseEntity<Page<TarefaViewDto>> searchPagination(@RequestBody SearchPaginationRequest<TarefaSearchFilter> request) {
        return ResponseEntity.ok(
                tarefaViewService.searchPagination(request.filter(), request.pageOrDefault(), request.sizeOrDefault())
        );
    }
}
