package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.filter.TarefaSearchFilter;
import br.com.rennataarruda.todolist.mapper.TarefaViewMapper;
import br.com.rennataarruda.todolist.repository.view.TarefaViewRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaViewServiceTest {

    @Mock
    private TarefaViewRepository repository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private final TarefaViewMapper mapper = new TarefaViewMapper();

    @Test
    void shouldSearchUsingScopedSpecification() {
        TarefaViewService service = new TarefaViewService(repository, authenticatedUserProvider, mapper);
        when(repository.findAll(any(Specification.class))).thenReturn(List.of());

        service.search(new TarefaSearchFilter(null, 1L, null, "titulo", null, null, null, null,
                null, true, null, null, null, null, null, null));

        verify(repository).findAll(any(Specification.class));
    }
}
