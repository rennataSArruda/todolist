package br.com.rennataarruda.todolist.service.commons;

import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractSearchCrudService<Entity, Id, Dto, Filter> extends AbstractCrudService<Entity, Id, Dto> {

    private final AbstractSearchService<Entity, Id, Dto, Filter> searchService;

    protected AbstractSearchCrudService(BaseRepository<Entity, Id> repository) {
        super(repository);
        this.searchService = new AbstractSearchService<>(repository) {
            @Override
            protected Dto toDto(Entity entity) {
                return AbstractSearchCrudService.this.toDto(entity);
            }

            @Override
            protected void addSearchPredicates(
                    List<Predicate> predicates,
                    Root<Entity> root,
                    CriteriaBuilder criteriaBuilder,
                    Filter filter
            ) {
                AbstractSearchCrudService.this.addSearchPredicates(predicates, root, criteriaBuilder, filter);
            }
        };
    }

    public List<Dto> search(Filter filter) {
        return searchService.search(filter);
    }

    public org.springframework.data.domain.Page<Dto> searchPagination(Filter filter, int page, int size) {
        return searchService.searchPagination(filter, page, size);
    }

    protected abstract void addSearchPredicates(
            List<Predicate> predicates,
            Root<Entity> root,
            CriteriaBuilder criteriaBuilder,
            Filter filter
    );
}
