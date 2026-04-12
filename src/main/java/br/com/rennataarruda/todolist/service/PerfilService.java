package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.PerfilDto;
import br.com.rennataarruda.todolist.dto.filter.PerfilSearchFilter;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.Perfil_;
import br.com.rennataarruda.todolist.mapper.PerfilMapper;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.service.commons.AbstractSearchCrudService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PerfilService extends AbstractSearchCrudService<Perfil, Long, PerfilDto, PerfilSearchFilter> {

    private final PerfilRepository repository;
    private final PerfilMapper mapper;

    public PerfilService(PerfilRepository repository, PerfilMapper mapper) {
        super(repository);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected void validateForCreate(PerfilDto dto) {
        validateRequiredFields(dto.codigo(), dto.descricao());

        if (repository.existsByCodigo(dto.codigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codigo de perfil ja cadastrado");
        }
    }

    @Override
    protected void validateForUpdate(Long id, PerfilDto dto) {
        validateRequiredFields(dto.codigo(), dto.descricao());

        if (repository.existsByCodigoAndIdNot(dto.codigo(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codigo de perfil ja cadastrado");
        }
    }

    @Override
    protected PerfilDto toDto(Perfil perfil) {
        return mapper.toDto(perfil);
    }

    @Override
    protected Perfil toNewEntity(PerfilDto dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntity(Perfil perfil, PerfilDto dto) {
        mapper.updateEntity(perfil, dto);
    }

    @Override
    protected void addSearchPredicates(
            List<Predicate> predicates,
            Root<Perfil> root,
            CriteriaBuilder criteriaBuilder,
            PerfilSearchFilter filter
    ) {
        if (filter == null) {
            return;
        }

        if (StringUtils.hasText(filter.codigo())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Perfil_.codigo)),
                    "%" + filter.codigo().toLowerCase() + "%"
            ));
        }

        if (StringUtils.hasText(filter.descricao())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Perfil_.descricao)),
                    "%" + filter.descricao().toLowerCase() + "%"
            ));
        }
    }

    @Override
    protected String notFoundMessage() {
        return "Perfil nao encontrado";
    }

    private void validateRequiredFields(String codigo, String descricao) {
        if (!StringUtils.hasText(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codigo e obrigatorio");
        }

        if (!StringUtils.hasText(descricao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descricao e obrigatoria");
        }
    }
}
