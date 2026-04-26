package br.com.rennataarruda.todolist.service.commons;

import br.com.rennataarruda.todolist.entity.commons.UsuarioScopedEntity;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUsuarioScopedSearchCrudService<
        Entity extends UsuarioScopedEntity,
        Id,
        Dto,
        Filter
        > extends AbstractSearchCrudService<Entity, Id, Dto, Filter> {

    private static final String USUARIO_ID_FIELD = "usuarioId";

    private final AuthenticatedUserProvider authenticatedUserProvider;

    protected AbstractUsuarioScopedSearchCrudService(
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

    @Transactional
    public void delete(Id id) {
        repository().delete(findByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<Dto> search(Filter filter) {
        return repository().findAll(buildScopedSpecification(filter))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<Dto> searchPagination(Filter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository().findAll(buildScopedSpecification(filter), pageable)
                .map(this::toDto);
    }

    @Override
    protected Entity findByIdOrThrow(Id id) {
        return repository().findOne(buildScopedByIdSpecification(id))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        notFoundMessage()
                ));
    }

    protected Specification<Entity> buildScopedSpecification(Filter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(usuarioScopePredicate(root, criteriaBuilder));
            addSearchPredicates(predicates, root, criteriaBuilder, filter);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected Specification<Entity> buildScopedByIdSpecification(Id id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                idEquals(root, criteriaBuilder, id),
                usuarioScopePredicate(root, criteriaBuilder)
        );
    }

    protected Specification<Entity> usuarioScopeSpecification() {
        return (root, query, criteriaBuilder) -> usuarioScopePredicate(root, criteriaBuilder);
    }

    protected Long currentUserId() {
        return authenticatedUserProvider.currentUserId();
    }

    private Predicate usuarioScopePredicate(Root<Entity> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(USUARIO_ID_FIELD), currentUserId());
    }

    protected abstract Predicate idEquals(Root<Entity> root, CriteriaBuilder criteriaBuilder, Id id);

    protected abstract void addSearchPredicates(
            List<Predicate> predicates,
            Root<Entity> root,
            CriteriaBuilder criteriaBuilder,
            Filter filter
    );
}
