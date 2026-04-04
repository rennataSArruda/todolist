package br.com.rennataarruda.todolist.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.dto.filter.UsuarioSearchFilter;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.mapper.UsuarioMapper;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
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
public class UsuarioService extends AbstractSearchCrudService<Usuario, Long, UsuarioDto, UsuarioSearchFilter> {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper) {
        super(repository);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected void validateForCreate(UsuarioDto dto) {
        validateRequiredFieldsForCreate(dto.username(), dto.password());

        if (repository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username ja cadastrado");
        }
    }

    @Override
    protected void validateForUpdate(Long id, UsuarioDto dto) {
        validateRequiredFieldsForUpdate(dto.username());

        if (repository.existsByUsernameAndIdNot(dto.username(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username ja cadastrado");
        }
    }

    @Override
    protected UsuarioDto toDto(Usuario usuario) {
        return mapper.toDto(usuario);
    }

    @Override
    protected Usuario toNewEntity(UsuarioDto dto) {
        return mapper.toEntity(dto, encodePassword(dto.password()));
    }

    @Override
    protected void updateEntity(Usuario usuario, UsuarioDto dto) {
        mapper.updateEntity(usuario, dto);
    }

    @Override
    protected void addSearchPredicates(
            List<Predicate> predicates,
            Root<Usuario> root,
            CriteriaBuilder criteriaBuilder,
            UsuarioSearchFilter filter
    ) {
        if (StringUtils.hasText(filter.username())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")),
                    "%" + filter.username().toLowerCase() + "%"
            ));
        }

        if (StringUtils.hasText(filter.name())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + filter.name().toLowerCase() + "%"
            ));
        }
    }

    private void validateRequiredFieldsForCreate(String username, String password) {
        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username e obrigatorio");
        }

        if (!StringUtils.hasText(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password e obrigatorio");
        }
    }

    private void validateRequiredFieldsForUpdate(String username) {
        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username e obrigatorio");
        }
    }

    private String encodePassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

}
