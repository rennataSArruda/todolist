package br.com.rennataarruda.todolist.service.commons;

import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public abstract class AbstractCrudService<Entity, Id, Dto> {

    private final BaseRepository<Entity, Id> repository;

    protected AbstractCrudService(BaseRepository<Entity, Id> repository) {
        this.repository = repository;
    }

    protected BaseRepository<Entity, Id> repository() {
        return repository;
    }

    public List<Dto> get() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public Dto getById(Id id) {
        return toDto(findByIdOrThrow(id));
    }

    public Dto create(Dto dto) {
        validateForCreate(dto);
        return toDto(repository.save(toNewEntity(dto)));
    }

    public Dto update(Id id, Dto dto) {
        validateForUpdate(id, dto);
        Entity entity = findByIdOrThrow(id);
        updateEntity(entity, dto);
        return toDto(repository.save(entity));
    }

    protected Entity findByIdOrThrow(Id id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage()));
    }

    protected void validateForCreate(Dto dto) {
    }

    protected void validateForUpdate(Id id, Dto dto) {
    }

    protected String notFoundMessage() {
        return "Registro nao encontrado";
    }

    protected abstract Dto toDto(Entity entity);

    protected abstract Entity toNewEntity(Dto dto);

    protected abstract void updateEntity(Entity entity, Dto dto);
}
