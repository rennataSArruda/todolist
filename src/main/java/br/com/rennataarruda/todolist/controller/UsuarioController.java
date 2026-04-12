package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.controller.commons.AbstractSearchCrudController;
import br.com.rennataarruda.todolist.controller.commons.SearchPaginationRequest;
import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.dto.filter.UsuarioSearchFilter;
import br.com.rennataarruda.todolist.security.authorization.PodeCriarUsuario;
import br.com.rennataarruda.todolist.security.authorization.PodeEditarUsuario;
import br.com.rennataarruda.todolist.security.authorization.PodeVisualizarUsuario;
import br.com.rennataarruda.todolist.service.UsuarioService;
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
@RequestMapping("api/usuario")
public class UsuarioController extends AbstractSearchCrudController<Long, UsuarioDto, UsuarioSearchFilter, UsuarioService> {

    public UsuarioController(UsuarioService service) {
        super(service);
    }

    @Override
    @GetMapping
    @PodeVisualizarUsuario
    public ResponseEntity<List<UsuarioDto>> get() {
        return super.get();
    }

    @Override
    @GetMapping("/{id}")
    @PodeVisualizarUsuario
    public ResponseEntity<UsuarioDto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PostMapping
    @PodeCriarUsuario
    public ResponseEntity<UsuarioDto> create(@RequestBody UsuarioDto dto) {
        return super.create(dto);
    }

    @Override
    @PutMapping("/{id}")
    @PodeEditarUsuario
    public ResponseEntity<UsuarioDto> update(@PathVariable Long id, @RequestBody UsuarioDto dto) {
        return super.update(id, dto);
    }

    @Override
    @PostMapping("/search")
    @PodeVisualizarUsuario
    public ResponseEntity<List<UsuarioDto>> search(@RequestBody UsuarioSearchFilter filter) {
        return super.search(filter);
    }

    @Override
    @PostMapping("/search-pagination")
    @PodeVisualizarUsuario
    public ResponseEntity<Page<UsuarioDto>> searchPagination(@RequestBody SearchPaginationRequest<UsuarioSearchFilter> request) {
        return super.searchPagination(request);
    }
}
