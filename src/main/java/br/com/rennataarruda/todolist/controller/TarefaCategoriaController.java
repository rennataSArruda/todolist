package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.controller.commons.AbstractSearchCrudController;
import br.com.rennataarruda.todolist.controller.commons.SearchPaginationRequest;
import br.com.rennataarruda.todolist.dto.TarefaCategoriaDto;
import br.com.rennataarruda.todolist.dto.filter.TarefaCategoriaSearchFilter;
import br.com.rennataarruda.todolist.security.authorization.PodeBloquearTarefaCategoria;
import br.com.rennataarruda.todolist.security.authorization.PodeCriarTarefaCategoria;
import br.com.rennataarruda.todolist.security.authorization.PodeEditarTarefaCategoria;
import br.com.rennataarruda.todolist.security.authorization.PodeVisualizarTarefaCategoria;
import br.com.rennataarruda.todolist.service.TarefaCategoriaService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tarefa-categoria")
public class TarefaCategoriaController extends AbstractSearchCrudController<
        Long,
        TarefaCategoriaDto,
        TarefaCategoriaSearchFilter,
        TarefaCategoriaService
        > {

    public TarefaCategoriaController(TarefaCategoriaService service) {
        super(service);
    }

    @Override
    @GetMapping
    @PodeVisualizarTarefaCategoria
    public ResponseEntity<List<TarefaCategoriaDto>> get() {
        return super.get();
    }

    @Override
    @GetMapping("/{id}")
    @PodeVisualizarTarefaCategoria
    public ResponseEntity<TarefaCategoriaDto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PostMapping
    @PodeCriarTarefaCategoria
    public ResponseEntity<TarefaCategoriaDto> create(@RequestBody TarefaCategoriaDto dto) {
        return super.create(dto);
    }

    @Override
    @PutMapping("/{id}")
    @PodeEditarTarefaCategoria
    public ResponseEntity<TarefaCategoriaDto> update(@PathVariable Long id, @RequestBody TarefaCategoriaDto dto) {
        return super.update(id, dto);
    }

    @PutMapping("/{id}/bloquear")
    @PodeBloquearTarefaCategoria
    public ResponseEntity<TarefaCategoriaDto> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(service().bloquear(id));
    }

    @Override
    @PostMapping("/search")
    @PodeVisualizarTarefaCategoria
    public ResponseEntity<List<TarefaCategoriaDto>> search(@RequestBody TarefaCategoriaSearchFilter filter) {
        return super.search(filter);
    }

    @Override
    @PostMapping("/search-pagination")
    @PodeVisualizarTarefaCategoria
    public ResponseEntity<Page<TarefaCategoriaDto>> searchPagination(
            @RequestBody SearchPaginationRequest<TarefaCategoriaSearchFilter> request
    ) {
        return super.searchPagination(request);
    }
}
