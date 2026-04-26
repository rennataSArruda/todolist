package br.com.rennataarruda.todolist.service.commons;

import br.com.rennataarruda.todolist.entity.commons.AbstractTabelaFixaEntity;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractTabelaFixaService<
        Entity extends AbstractTabelaFixaEntity<Id>,
        Id extends Serializable,
        Dto
        > {

    private final BaseRepository<Entity, Id> repository;

    protected AbstractTabelaFixaService(BaseRepository<Entity, Id> repository) {
        this.repository = repository;
    }

    protected BaseRepository<Entity, Id> repository() {
        return repository;
    }

    @Transactional(readOnly = true)
    public Dto load(Id id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage()));
    }

    @Transactional(readOnly = true)
    public List<Dto> loadAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    protected String notFoundMessage() {
        return "Registro nao encontrado";
    }

    protected abstract Dto toDto(Entity entity);
}
