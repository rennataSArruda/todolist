package br.com.rennataarruda.todolist.controller.commons;

import br.com.rennataarruda.todolist.service.commons.AbstractTabelaFixaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractTabelaFixaResource<
        Id extends Serializable,
        Dto,
        Service extends AbstractTabelaFixaService<?, Id, Dto>
        > {

    private final Service service;

    protected AbstractTabelaFixaResource(Service service) {
        this.service = service;
    }

    protected Service service() {
        return service;
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<Dto> load(@PathVariable Id id) {
        return ResponseEntity.ok(service.load(id));
    }

    @GetMapping("/load-all")
    public ResponseEntity<List<Dto>> loadAll() {
        return ResponseEntity.ok(service.loadAll());
    }
}
