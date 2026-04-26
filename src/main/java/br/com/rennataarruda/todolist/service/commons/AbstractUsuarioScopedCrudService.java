package br.com.rennataarruda.todolist.service.commons;

import br.com.rennataarruda.todolist.entity.commons.UsuarioScopedEntity;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public abstract class AbstractUsuarioScopedCrudService<Entity extends UsuarioScopedEntity, Id, Dto>
        extends AbstractCrudService<Entity, Id, Dto> {

    private static final String USUARIO_ID_FIELD = "usuarioId";

    private final AuthenticatedUserProvider authenticatedUserProvider;

    protected AbstractUsuarioScopedCrudService(
            BaseRepository<Entity, Id> repository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        super(repository);
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dto> get() {
        return repository().findAll(usuarioScopeSpecification())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Dto getById(Id id) {
        return toDto(findByIdOrThrow(id));
    }

    @Override
    @Transactional
    public Dto create(Dto dto) {
        validateForCreate(dto);
        Entity entity = toNewEntity(dto);
        entity.definirUsuarioId(currentUserId());
        return toDto(repository().save(entity));
    }

    @Override
    @Transactional
    public Dto update(Id id, Dto dto) {
        validateForUpdate(id, dto);
        Entity entity = findByIdOrThrow(id);
        updateEntity(entity, dto);
        entity.definirUsuarioId(currentUserId());
        return toDto(repository().save(entity));
    }

    @Override
    protected Entity findByIdOrThrow(Id id) {
        return repository().findOne(buildScopedByIdSpecification(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage()));
    }

    protected org.springframework.data.jpa.domain.Specification<Entity> buildScopedByIdSpecification(Id id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                idEquals(root, criteriaBuilder, id),
                usuarioScopePredicate(root, criteriaBuilder)
        );
    }

    protected org.springframework.data.jpa.domain.Specification<Entity> usuarioScopeSpecification() {
        return (root, query, criteriaBuilder) -> usuarioScopePredicate(root, criteriaBuilder);
    }

    protected Long currentUserId() {
        return authenticatedUserProvider.currentUserId();
    }

    private Predicate usuarioScopePredicate(Root<Entity> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(USUARIO_ID_FIELD), currentUserId());
    }

    protected abstract Predicate idEquals(Root<Entity> root, CriteriaBuilder criteriaBuilder, Id id);
}
