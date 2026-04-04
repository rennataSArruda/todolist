package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.dto.filter.UsuarioSearchFilter;
import br.com.rennataarruda.todolist.controller.commons.AbstractSearchCrudController;
import br.com.rennataarruda.todolist.service.UsuarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController extends AbstractSearchCrudController<Long, UsuarioDto, UsuarioSearchFilter, UsuarioService> {

    public UsuarioController(UsuarioService service) {
        super(service);
    }
}
