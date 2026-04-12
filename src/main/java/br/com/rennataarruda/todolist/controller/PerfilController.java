package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.controller.commons.AbstractSearchCrudController;
import br.com.rennataarruda.todolist.dto.PerfilDto;
import br.com.rennataarruda.todolist.dto.filter.PerfilSearchFilter;
import br.com.rennataarruda.todolist.security.authorization.ApenasRoot;
import br.com.rennataarruda.todolist.service.PerfilService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApenasRoot
@RestController
@RequestMapping("api/perfil")
public class PerfilController extends AbstractSearchCrudController<Long, PerfilDto, PerfilSearchFilter, PerfilService> {

    public PerfilController(PerfilService service) {
        super(service);
    }
}
