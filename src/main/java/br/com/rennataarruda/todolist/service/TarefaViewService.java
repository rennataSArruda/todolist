package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.filter.TarefaSearchFilter;
import br.com.rennataarruda.todolist.dto.view.TarefaViewDto;
import br.com.rennataarruda.todolist.entity.view.TarefaView;
import br.com.rennataarruda.todolist.entity.view.TarefaView_;
import br.com.rennataarruda.todolist.mapper.TarefaViewMapper;
import br.com.rennataarruda.todolist.repository.view.TarefaViewRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TarefaViewService {

    private final TarefaViewRepository repository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final TarefaViewMapper mapper;

    public TarefaViewService(
            TarefaViewRepository repository,
            AuthenticatedUserProvider authenticatedUserProvider,
            TarefaViewMapper mapper
    ) {
        this.repository = repository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<TarefaViewDto> search(TarefaSearchFilter filter) {
        return repository.findAll(buildSpecification(filter))
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TarefaViewDto> searchPagination(TarefaSearchFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(buildSpecification(filter), pageable)
                .map(mapper::toDto);
    }

    protected Specification<TarefaView> buildSpecification(TarefaSearchFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get(TarefaView_.USUARIO_ID), authenticatedUserProvider.currentUserId()));
            addSearchPredicates(predicates, root, criteriaBuilder, filter);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addSearchPredicates(
            List<Predicate> predicates,
            Root<TarefaView> root,
            CriteriaBuilder criteriaBuilder,
            TarefaSearchFilter filter
    ) {
        if (filter == null) {
            return;
        }

        addEqualsPredicate(predicates, root, criteriaBuilder, TarefaView_.CATEGORIA_ID, filter.categoriaId());
        addEqualsPredicate(predicates, root, criteriaBuilder, TarefaView_.STATUS_ID, filter.statusId());
        addEqualsPredicate(predicates, root, criteriaBuilder, TarefaView_.PRIORIDADE_ID, filter.prioridadeId());
        addEqualsPredicate(predicates, root, criteriaBuilder, TarefaView_.IMPORTANTE, filter.importante());
        addEqualsPredicate(predicates, root, criteriaBuilder, TarefaView_.ATIVO, filter.ativo());
        addLikePredicate(predicates, root, criteriaBuilder, TarefaView_.TITULO, filter.titulo());
        addLikePredicate(predicates, root, criteriaBuilder, TarefaView_.DESCRICAO, filter.descricao());
        addLikePredicate(predicates, root, criteriaBuilder, TarefaView_.CATEGORIA_NOME, filter.categoriaNome());
        addLikePredicate(predicates, root, criteriaBuilder, TarefaView_.STATUS_CODIGO, filter.statusCodigo());
        addLikePredicate(predicates, root, criteriaBuilder, TarefaView_.PRIORIDADE_CODIGO, filter.prioridadeCodigo());
        addPeriodPredicate(predicates, root, criteriaBuilder, TarefaView_.DATA_INICIO, filter.dataInicioDe(), filter.dataInicioAte());
        addPeriodPredicate(predicates, root, criteriaBuilder, TarefaView_.DATA_FIM, filter.dataFimDe(), filter.dataFimAte());
        addPeriodPredicate(
                predicates,
                root,
                criteriaBuilder,
                TarefaView_.DATA_CONCLUSAO,
                filter.dataConclusaoDe(),
                filter.dataConclusaoAte()
        );
    }

    private <T> void addEqualsPredicate(
            List<Predicate> predicates,
            Root<TarefaView> root,
            CriteriaBuilder criteriaBuilder,
            String field,
            T value
    ) {
        if (value != null) {
            predicates.add(criteriaBuilder.equal(root.get(field), value));
        }
    }

    private void addLikePredicate(
            List<Predicate> predicates,
            Root<TarefaView> root,
            CriteriaBuilder criteriaBuilder,
            String field,
            String value
    ) {
        if (StringUtils.hasText(value)) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(field)),
                    "%" + value.toLowerCase() + "%"
            ));
        }
    }

    private void addPeriodPredicate(
            List<Predicate> predicates,
            Root<TarefaView> root,
            CriteriaBuilder criteriaBuilder,
            String field,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (start != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(field), start));
        }

        if (end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(field), end));
        }
    }
}
