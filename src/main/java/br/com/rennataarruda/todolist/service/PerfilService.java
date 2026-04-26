package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.PerfilDto;
import br.com.rennataarruda.todolist.dto.PerfilPermissaoDto;
import br.com.rennataarruda.todolist.dto.filter.PerfilSearchFilter;
import br.com.rennataarruda.todolist.entity.Papel;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.PerfilPapelPermissao;
import br.com.rennataarruda.todolist.entity.Perfil_;
import br.com.rennataarruda.todolist.entity.Permissao;
import br.com.rennataarruda.todolist.mapper.PerfilMapper;
import br.com.rennataarruda.todolist.repository.PapelPermissaoRepository;
import br.com.rennataarruda.todolist.repository.PapelRepository;
import br.com.rennataarruda.todolist.repository.PerfilPapelPermissaoRepository;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.repository.PermissaoRepository;
import br.com.rennataarruda.todolist.service.commons.AbstractSearchCrudService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class PerfilService extends AbstractSearchCrudService<Perfil, Long, PerfilDto, PerfilSearchFilter> {

    private final PerfilRepository repository;
    private final PapelRepository papelRepository;
    private final PermissaoRepository permissaoRepository;
    private final PerfilPapelPermissaoRepository perfilPapelPermissaoRepository;
    private final PapelPermissaoRepository papelPermissaoRepository;
    private final PerfilMapper mapper;

    public PerfilService(
            PerfilRepository repository,
            PapelRepository papelRepository,
            PermissaoRepository permissaoRepository,
            PerfilPapelPermissaoRepository perfilPapelPermissaoRepository,
            PapelPermissaoRepository papelPermissaoRepository,
            PerfilMapper mapper
    ) {
        super(repository);
        this.repository = repository;
        this.papelRepository = papelRepository;
        this.permissaoRepository = permissaoRepository;
        this.perfilPapelPermissaoRepository = perfilPapelPermissaoRepository;
        this.papelPermissaoRepository = papelPermissaoRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfilDto> get() {
        return super.get();
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilDto getById(Long id) {
        return super.getById(id);
    }

    @Override
    @Transactional
    public PerfilDto create(PerfilDto dto) {
        validateForCreate(dto);
        Perfil perfil = repository.save(toNewEntity(dto));
        sincronizarPermissoes(perfil, dto.permissoes());
        return toDto(perfil);
    }

    @Override
    @Transactional
    public PerfilDto update(Long id, PerfilDto dto) {
        validateForUpdate(id, dto);
        Perfil perfil = findByIdOrThrow(id);
        updateEntity(perfil, dto);

        if (dto.permissoes() != null) {
            perfilPapelPermissaoRepository.deleteByPerfilId(id);
            perfil.getAutorizacoes().clear();
            sincronizarPermissoes(perfil, dto.permissoes());
        }

        return toDto(repository.save(perfil));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfilDto> search(PerfilSearchFilter filter) {
        return super.search(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerfilDto> searchPagination(PerfilSearchFilter filter, int page, int size) {
        return super.searchPagination(filter, page, size);
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

    private void sincronizarPermissoes(Perfil perfil, List<PerfilPermissaoDto> permissoes) {
        if (permissoes == null || permissoes.isEmpty()) {
            return;
        }

        Set<String> chaves = new LinkedHashSet<>();
        List<PerfilPapelPermissao> autorizacoes = permissoes.stream()
                .map(permissaoDto -> toAutorizacao(perfil, permissaoDto, chaves))
                .toList();

        perfil.getAutorizacoes().addAll(autorizacoes);
        perfilPapelPermissaoRepository.saveAll(autorizacoes);
    }

    private PerfilPapelPermissao toAutorizacao(
            Perfil perfil,
            PerfilPermissaoDto permissaoDto,
            Set<String> chaves
    ) {
        validarPermissao(permissaoDto);

        String papelCodigo = permissaoDto.papelCodigo().trim().toUpperCase();
        String permissaoCodigo = permissaoDto.permissaoCodigo().trim().toUpperCase();
        String chave = papelCodigo + "_" + permissaoCodigo;

        if (!chaves.add(chave)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permissao duplicada no perfil: " + chave);
        }

        if (!papelPermissaoRepository.existsByPapelCodigoAndPermissaoCodigo(papelCodigo, permissaoCodigo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O papel " + papelCodigo + " nao possui permissao para " + permissaoCodigo
            );
        }

        Papel papel = papelRepository.findByCodigo(papelCodigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Papel nao encontrado: " + papelCodigo
                ));

        Permissao permissao = permissaoRepository.findByCodigo(permissaoCodigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Permissao nao encontrada: " + permissaoCodigo
                ));

        return new PerfilPapelPermissao(perfil, papel, permissao);
    }

    private void validarPermissao(PerfilPermissaoDto permissaoDto) {
        if (permissaoDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permissao do perfil e obrigatoria");
        }

        if (!StringUtils.hasText(permissaoDto.papelCodigo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codigo do papel e obrigatorio");
        }

        if (!StringUtils.hasText(permissaoDto.permissaoCodigo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codigo da permissao e obrigatorio");
        }
    }
}
