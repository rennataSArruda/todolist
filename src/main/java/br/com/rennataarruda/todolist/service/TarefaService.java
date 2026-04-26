package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.TarefaDto;
import br.com.rennataarruda.todolist.entity.Tarefa;
import br.com.rennataarruda.todolist.entity.Tarefa_;
import br.com.rennataarruda.todolist.mapper.TarefaMapper;
import br.com.rennataarruda.todolist.entity.fixed.enumerations.TarefaStatusEnum;
import br.com.rennataarruda.todolist.repository.TarefaRepository;

import br.com.rennataarruda.todolist.repository.TarefaCategoriaRepository;
import br.com.rennataarruda.todolist.repository.fixed.TarefaPrioridadeRepository;
import br.com.rennataarruda.todolist.repository.fixed.TarefaStatusRepository;
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
    private final TarefaStatusRepository statusRepository;
    private final TarefaPrioridadeRepository prioridadeRepository;
    private final TarefaCategoriaRepository categoriaRepository;

    public TarefaService(
            TarefaRepository repository,
            AuthenticatedUserProvider authenticatedUserProvider,
            TarefaMapper mapper,
            TarefaStatusRepository statusRepository,
            TarefaPrioridadeRepository prioridadeRepository,
            TarefaCategoriaRepository categoriaRepository
    ) {
        super(repository, authenticatedUserProvider);
        this.mapper = mapper;
        this.statusRepository = statusRepository;
        this.prioridadeRepository = prioridadeRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    protected void validateForCreate(TarefaDto dto) {
        validateBusinessRules(dto);
    }

    @Override
    protected void validateForUpdate(Long id, TarefaDto dto) {
        validateBusinessRules(dto);
    }

    private void validateBusinessRules(TarefaDto dto) {
        if (dto == null || !StringUtils.hasText(dto.titulo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titulo e obrigatorio");
        }

        if (dto.statusId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status e obrigatorio");
        }
        var status = statusRepository.findById(dto.statusId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status nao encontrado"));
        if (!Boolean.TRUE.equals(status.getAtivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inativo");
        }

        if (dto.prioridadeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade e obrigatoria");
        }
        var prioridade = prioridadeRepository.findById(dto.prioridadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade nao encontrada"));
        if (!Boolean.TRUE.equals(prioridade.getAtivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade inativa");
        }

        if (dto.categoriaId() != null) {
            var categoria = categoriaRepository.findById(dto.categoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria nao encontrada"));
            if (!categoria.getUsuarioId().equals(currentUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Categoria nao pertence ao usuario");
            }
        }

        if (dto.dataInicio() != null && dto.dataFim() != null && dto.dataInicio().isAfter(dto.dataFim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inicio nao pode ser maior que data fim");
        }

        if (dto.dataInicio() != null && dto.dataConclusao() != null && dto.dataConclusao().isBefore(dto.dataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data conclusao nao pode ser anterior a data inicio");
        }
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
        Long statusId = dto.statusId();
        if (dto.dataConclusao() != null) {
            statusId = TarefaStatusEnum.CONCLUIDA.getId();
        }
        mapper.updateEntity(entity, dto);
        entity.setStatusId(statusId);
    }

    @Override
    protected String notFoundMessage() {
        return "Tarefa nao encontrada";
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


}
