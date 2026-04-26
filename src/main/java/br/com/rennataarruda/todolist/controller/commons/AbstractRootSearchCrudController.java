package br.com.rennataarruda.todolist.controller.commons;

import br.com.rennataarruda.todolist.security.authorization.ApenasRoot;
import br.com.rennataarruda.todolist.service.commons.AbstractSearchCrudService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@ApenasRoot
public abstract class AbstractRootSearchCrudController<
        Id,
        Dto,
        Filter,
        Service extends AbstractSearchCrudService<?, Id, Dto, Filter>
        > extends AbstractSearchCrudController<Id, Dto, Filter, Service> {

    protected AbstractRootSearchCrudController(Service service) {
        super(service);
    }

    @Override
    @GetMapping
    @ApenasRoot
    public ResponseEntity<List<Dto>> get() {
        return super.get();
    }

    @Override
    @GetMapping("/{id}")
    @ApenasRoot
    public ResponseEntity<Dto> getById(@PathVariable Id id) {
        return super.getById(id);
    }

    @Override
    @PostMapping
    @ApenasRoot
    public ResponseEntity<Dto> create(@RequestBody Dto dto) {
        return super.create(dto);
    }

    @Override
    @PutMapping("/{id}")
    @ApenasRoot
    public ResponseEntity<Dto> update(@PathVariable Id id, @RequestBody Dto dto) {
        return super.update(id, dto);
    }

    @Override
    @PostMapping("/search")
    @ApenasRoot
    public ResponseEntity<List<Dto>> search(@RequestBody Filter filter) {
        return super.search(filter);
    }

    @Override
    @PostMapping("/search-pagination")
    @ApenasRoot
    public ResponseEntity<Page<Dto>> searchPagination(@RequestBody SearchPaginationRequest<Filter> request) {
        return super.searchPagination(request);
    }
}
