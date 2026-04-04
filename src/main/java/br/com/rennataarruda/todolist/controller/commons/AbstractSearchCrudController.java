package br.com.rennataarruda.todolist.controller.commons;

import br.com.rennataarruda.todolist.service.commons.AbstractSearchCrudService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class AbstractSearchCrudController<Id, Dto, Filter, Service extends AbstractSearchCrudService<?, Id, Dto, Filter>>
        extends AbstractCrudController<Id, Dto, Service> {

    private final Service service;

    protected AbstractSearchCrudController(Service service) {
        super(service);
        this.service = service;
    }

    @PostMapping("/search")
    public ResponseEntity<List<Dto>> search(@RequestBody Filter filter) {
        return ResponseEntity.ok(service.search(filter));
    }

    @PostMapping("/search-pagination")
    public ResponseEntity<Page<Dto>> searchPagination(@RequestBody SearchPaginationRequest<Filter> request) {
        return ResponseEntity.ok(
                service.searchPagination(request.filter(), request.pageOrDefault(), request.sizeOrDefault())
        );
    }
}
