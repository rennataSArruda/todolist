package br.com.rennataarruda.todolist.service.email;

import br.com.rennataarruda.todolist.dto.EmailConfigDto;
import br.com.rennataarruda.todolist.dto.filter.EmailConfigSearchFilter;
import br.com.rennataarruda.todolist.entity.EmailConfig;
import br.com.rennataarruda.todolist.entity.EmailConfig_;
import br.com.rennataarruda.todolist.mapper.EmailConfigMapper;
import br.com.rennataarruda.todolist.repository.EmailConfigRepository;
import br.com.rennataarruda.todolist.service.ApplicationCryptoService;
import br.com.rennataarruda.todolist.service.commons.AbstractSearchCrudService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class EmailConfigService extends AbstractSearchCrudService<
        EmailConfig,
        Long,
        EmailConfigDto,
        EmailConfigSearchFilter
        > {

    private final EmailConfigRepository repository;
    private final EmailConfigMapper mapper;
    private final ApplicationCryptoService cryptoService;

    public EmailConfigService(
            EmailConfigRepository repository,
            EmailConfigMapper mapper,
            ApplicationCryptoService cryptoService
    ) {
        super(repository);
        this.repository = repository;
        this.mapper = mapper;
        this.cryptoService = cryptoService;
    }

    @Override
    @Transactional
    public EmailConfigDto create(EmailConfigDto dto) {
        validateForCreate(dto);
        EmailConfig entity = toNewEntity(dto);
        if (Boolean.TRUE.equals(entity.getAtivo())) {
            deactivateActiveConfigs(null);
        }
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public EmailConfigDto update(Long id, EmailConfigDto dto) {
        validateForUpdate(id, dto);
        EmailConfig entity = findByIdOrThrow(id);
        updateEntity(entity, dto);
        if (Boolean.TRUE.equals(entity.getAtivo())) {
            deactivateActiveConfigs(id);
        }
        return toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public EmailConfigDto getByIdWithPassword(Long id) {
        EmailConfig entity = findByIdOrThrow(id);
        return mapper.toDtoWithPassword(entity, decryptPassword(entity.getPassword()));
    }

    @Transactional
    public EmailConfigDto ativar(Long id) {
        EmailConfig entity = findByIdOrThrow(id);
        deactivateActiveConfigs(id);
        entity.ativar();
        return toDto(repository.save(entity));
    }

    @Transactional
    public EmailConfigDto bloquear(Long id) {
        EmailConfig entity = findByIdOrThrow(id);
        entity.bloquear();
        return toDto(repository.save(entity));
    }

    public Optional<EmailConfig> findActiveConfig() {
        return repository.findFirstByAtivoTrueOrderByIdDesc();
    }

    public String decryptPassword(String password) {
        return cryptoService.decryptIfNeeded(password);
    }

    @Override
    protected void validateForCreate(EmailConfigDto dto) {
        validateRequiredFields(dto);
        if (Boolean.TRUE.equals(dto.auth()) && !StringUtils.hasText(dto.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha SMTP é obrigatória quando a autenticação está ativa");
        }
    }

    @Override
    protected void validateForUpdate(Long id, EmailConfigDto dto) {
        validateRequiredFields(dto);
    }

    @Override
    protected EmailConfigDto toDto(EmailConfig entity) {
        return mapper.toDto(entity);
    }

    @Override
    protected EmailConfig toNewEntity(EmailConfigDto dto) {
        EmailConfig entity = mapper.toEntity(dto);
        encryptPassword(entity);
        return entity;
    }

    @Override
    protected void updateEntity(EmailConfig entity, EmailConfigDto dto) {
        mapper.updateEntity(entity, dto);
        encryptPassword(entity);
    }

    @Override
    protected void addSearchPredicates(
            List<Predicate> predicates,
            Root<EmailConfig> root,
            CriteriaBuilder criteriaBuilder,
            EmailConfigSearchFilter filter
    ) {
        if (filter == null) {
            return;
        }

        if (StringUtils.hasText(filter.host())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(EmailConfig_.host)),
                    "%" + filter.host().toLowerCase() + "%"
            ));
        }

        if (StringUtils.hasText(filter.username())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(EmailConfig_.username)),
                    "%" + filter.username().toLowerCase() + "%"
            ));
        }

        if (StringUtils.hasText(filter.fromAddress())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(EmailConfig_.fromAddress)),
                    "%" + filter.fromAddress().toLowerCase() + "%"
            ));
        }

        if (filter.ativo() != null) {
            predicates.add(criteriaBuilder.equal(root.get(EmailConfig_.ativo), filter.ativo()));
        }
    }

    @Override
    protected String notFoundMessage() {
        return "Configuração de e-mail não encontrada";
    }

    private void validateRequiredFields(EmailConfigDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Configuração de e-mail é obrigatória");
        }

        if (!StringUtils.hasText(dto.host())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Host é obrigatório");
        }

        if (dto.port() == null || dto.port() < 1 || dto.port() > 65535) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Porta inválida");
        }

        if (!StringUtils.hasText(dto.fromAddress())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail do remetente é obrigatório");
        }

        if (Boolean.TRUE.equals(dto.auth()) && !StringUtils.hasText(dto.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário SMTP é obrigatório quando a autenticação está ativa");
        }
    }

    private void encryptPassword(EmailConfig entity) {
        entity.atualizar(
                entity.getHost(),
                entity.getPort(),
                entity.getUsername(),
                cryptoService.encryptIfNeeded(entity.getPassword()),
                entity.getFromAddress(),
                entity.getFromName(),
                entity.getAuth(),
                entity.getStartTls(),
                entity.getSsl(),
                entity.getAtivo()
        );
    }

    private void deactivateActiveConfigs(Long exceptId) {
        List<EmailConfig> activeConfigs = repository.findByAtivoTrue();
        activeConfigs.stream()
                .filter(config -> exceptId == null || !exceptId.equals(config.getId()))
                .forEach(EmailConfig::bloquear);
        repository.saveAll(activeConfigs);
    }
}
