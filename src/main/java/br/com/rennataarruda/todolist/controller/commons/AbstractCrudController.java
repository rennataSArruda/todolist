package br.com.rennataarruda.todolist.controller.commons;

import br.com.rennataarruda.todolist.service.commons.AbstractCrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class AbstractCrudController<Id, Dto, Service extends AbstractCrudService<?, Id, Dto>> {

    private final Service service;

    protected AbstractCrudController(Service service) {
        this.service = service;
    }

    protected Service service() {
        return service;
    }

    @GetMapping
    public ResponseEntity<List<Dto>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dto> getById(@PathVariable Id id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Dto> create(@RequestBody Dto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dto> update(@PathVariable Id id, @RequestBody Dto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
}
