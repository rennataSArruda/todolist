package br.com.rennataarruda.todolist.service.commons;

import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSearchService<Entity, Id, Dto, Filter> {

    private final BaseRepository<Entity, Id> repository;

    protected AbstractSearchService(BaseRepository<Entity, Id> repository) {
        this.repository = repository;
    }

    protected BaseRepository<Entity, Id> repository() {
        return repository;
    }

    public List<Dto> search(Filter filter) {
        return repository.findAll(buildSpecification(filter))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Page<Dto> searchPagination(Filter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(buildSpecification(filter), pageable)
                .map(this::toDto);
    }

    protected Specification<Entity> buildSpecification(Filter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addSearchPredicates(predicates, root, criteriaBuilder, filter);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected abstract Dto toDto(Entity entity);

    protected abstract void addSearchPredicates(
            List<Predicate> predicates,
            Root<Entity> root,
            CriteriaBuilder criteriaBuilder,
            Filter filter
    );
}
