package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.controller.commons.AbstractRootSearchCrudController;
import br.com.rennataarruda.todolist.dto.EmailConfigDto;
import br.com.rennataarruda.todolist.dto.filter.EmailConfigSearchFilter;
import br.com.rennataarruda.todolist.security.authorization.ApenasRoot;
import br.com.rennataarruda.todolist.service.email.EmailConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/email-config")
public class EmailConfigController extends AbstractRootSearchCrudController<
        Long,
        EmailConfigDto,
        EmailConfigSearchFilter,
        EmailConfigService
        > {

    public EmailConfigController(EmailConfigService service) {
        super(service);
    }


    @Override
    @GetMapping("/{id}")
    @ApenasRoot
    public ResponseEntity<EmailConfigDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service().getByIdWithPassword(id));
    }

    @PutMapping("/{id}/ativar")
    @ApenasRoot
    public ResponseEntity<EmailConfigDto> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service().ativar(id));
    }

    @PutMapping("/{id}/bloquear")
    @ApenasRoot
    public ResponseEntity<EmailConfigDto> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(service().bloquear(id));
    }
}

