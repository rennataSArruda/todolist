package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.TarefaCategoriaDto;
import br.com.rennataarruda.todolist.dto.filter.TarefaCategoriaSearchFilter;
import br.com.rennataarruda.todolist.entity.TarefaCategoria;
import br.com.rennataarruda.todolist.entity.TarefaCategoria_;
import br.com.rennataarruda.todolist.mapper.TarefaCategoriaMapper;
import br.com.rennataarruda.todolist.repository.TarefaCategoriaRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import br.com.rennataarruda.todolist.service.commons.AbstractUsuarioScopedSearchCrudService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TarefaCategoriaService extends AbstractUsuarioScopedSearchCrudService<
        TarefaCategoria,
        Long,
        TarefaCategoriaDto,
        TarefaCategoriaSearchFilter
        > {

    private final TarefaCategoriaMapper mapper;

    public TarefaCategoriaService(
            TarefaCategoriaRepository repository,
            AuthenticatedUserProvider authenticatedUserProvider,
            TarefaCategoriaMapper mapper
    ) {
        super(repository, authenticatedUserProvider);
        this.mapper = mapper;
    }

    @Override
    protected void validateForCreate(TarefaCategoriaDto dto) {
        validateRequiredFields(dto);
    }

    @Override
    protected void validateForUpdate(Long id, TarefaCategoriaDto dto) {
        validateRequiredFields(dto);
    }

    @Override
    protected TarefaCategoriaDto toDto(TarefaCategoria entity) {
        return mapper.toDto(entity);
    }

    @Override
    protected TarefaCategoria toNewEntity(TarefaCategoriaDto dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntity(TarefaCategoria entity, TarefaCategoriaDto dto) {
        mapper.updateEntity(entity, dto);
    }

    @Transactional
    public TarefaCategoriaDto bloquear(Long id) {
        TarefaCategoria entity = findByIdOrThrow(id);
        entity.alternarAtivo();
        return toDto(repository().save(entity));
    }

    @Override
    protected Predicate idEquals(Root<TarefaCategoria> root, CriteriaBuilder criteriaBuilder, Long id) {
        return criteriaBuilder.equal(root.get(TarefaCategoria_.id), id);
    }

    @Override
    protected void addSearchPredicates(
            List<Predicate> predicates,
            Root<TarefaCategoria> root,
            CriteriaBuilder criteriaBuilder,
            TarefaCategoriaSearchFilter filter
    ) {
        if (filter == null) {
            return;
        }

        if (StringUtils.hasText(filter.nome())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(TarefaCategoria_.nome)),
                    "%" + filter.nome().toLowerCase() + "%"
            ));
        }

        if (StringUtils.hasText(filter.descricao())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(TarefaCategoria_.descricao)),
                    "%" + filter.descricao().toLowerCase() + "%"
            ));
        }

        if (filter.ativo() != null) {
            predicates.add(criteriaBuilder.equal(root.get(TarefaCategoria_.ativo), filter.ativo()));
        }
    }

    @Override
    protected String notFoundMessage() {
        return "Categoria da tarefa nao encontrada";
    }

    private void validateRequiredFields(TarefaCategoriaDto dto) {
        if (dto == null || !StringUtils.hasText(dto.nome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e obrigatorio");
        }
    }
}
