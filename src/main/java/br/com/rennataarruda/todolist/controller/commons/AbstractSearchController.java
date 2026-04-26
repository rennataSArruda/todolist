package br.com.rennataarruda.todolist.controller.commons;

import br.com.rennataarruda.todolist.security.authorization.ApenasRoot;
import br.com.rennataarruda.todolist.service.commons.AbstractSearchService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class AbstractSearchController<Dto, Filter, Service extends AbstractSearchService<?, ?, Dto, Filter>> {

    private final Service service;

    protected AbstractSearchController(Service service) {
        this.service = service;
    }

    protected Service service() {
        return service;
    }

    @PostMapping("/search")
    @ApenasRoot
    public ResponseEntity<List<Dto>> search(@RequestBody Filter filter) {
        return ResponseEntity.ok(service.search(filter));
    }

    @PostMapping("/search-pagination")
    @ApenasRoot
    public ResponseEntity<Page<Dto>> searchPagination(@RequestBody SearchPaginationRequest<Filter> request) {
        return ResponseEntity.ok(
                service.searchPagination(request.filter(), request.pageOrDefault(), request.sizeOrDefault())
        );
    }
}
