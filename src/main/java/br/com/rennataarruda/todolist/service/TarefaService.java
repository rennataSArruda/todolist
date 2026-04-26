package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.TarefaDto;
import br.com.rennataarruda.todolist.entity.Tarefa;
import br.com.rennataarruda.todolist.entity.Tarefa_;
import br.com.rennataarruda.todolist.mapper.TarefaMapper;
import br.com.rennataarruda.todolist.repository.TarefaRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import br.com.rennataarruda.todolist.service.commons.AbstractUsuarioScopedCrudService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TarefaService extends AbstractUsuarioScopedCrudService<Tarefa, Long, TarefaDto> {

    private final TarefaMapper mapper;

    public TarefaService(
            TarefaRepository repository,
            AuthenticatedUserProvider authenticatedUserProvider,
            TarefaMapper mapper
    ) {
        super(repository, authenticatedUserProvider);
        this.mapper = mapper;
    }

    @Override
    protected void validateForCreate(TarefaDto dto) {
        validateRequiredFields(dto);
    }

    @Override
    protected void validateForUpdate(Long id, TarefaDto dto) {
        validateRequiredFields(dto);
    }

    @Override
    protected TarefaDto toDto(Tarefa entity) {
        return mapper.toDto(entity);
    }

    @Override
    protected Tarefa toNewEntity(TarefaDto dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntity(Tarefa entity, TarefaDto dto) {
        mapper.updateEntity(entity, dto);
    }

    @Override
    protected Predicate idEquals(Root<Tarefa> root, CriteriaBuilder criteriaBuilder, Long id) {
        return criteriaBuilder.equal(root.get(Tarefa_.id), id);
    }

    @Transactional
    public TarefaDto bloquear(Long id) {
        Tarefa entity = findByIdOrThrow(id);
        entity.alternarAtivo();
        return toDto(repository().save(entity));
    }

    @Override
    protected String notFoundMessage() {
        return "Tarefa nao encontrada";
    }

    private void validateRequiredFields(TarefaDto dto) {
        if (dto == null || !StringUtils.hasText(dto.titulo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titulo e obrigatorio");
        }

        if (dto.statusId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status e obrigatorio");
        }

        if (dto.prioridadeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade e obrigatoria");
        }
    }
}
