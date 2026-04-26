package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorStatusIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaTotalIndicadorDto;
import br.com.rennataarruda.todolist.entity.view.TarefaAnaliticoView;
import br.com.rennataarruda.todolist.entity.view.TarefaAnaliticoView_;
import br.com.rennataarruda.todolist.repository.view.TarefaAnaliticoViewRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaAnaliticoViewService {

    private final TarefaAnaliticoViewRepository repository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public TarefaAnaliticoViewService(
            TarefaAnaliticoViewRepository repository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.repository = repository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Transactional(readOnly = true)
    public List<TarefaAnaliticoView> search(Specification<TarefaAnaliticoView> customSpecification) {
        return repository.findAll(buildScopedSpecification(customSpecification));
    }

    @Transactional(readOnly = true)
    public TarefaTotalIndicadorDto totalTarefasAtivasDoUsuarioLogado() {
        Specification<TarefaAnaliticoView> spec = buildScopedSpecification(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(TarefaAnaliticoView_.ATIVO), true)
        );
        Long totalTarefas = repository.count(spec);
        return new TarefaTotalIndicadorDto(totalTarefas);
    }

    @Transactional(readOnly = true)
    public List<TarefaPorStatusIndicadorDto> totalTarefasAtivasPorStatusDoUsuarioLogado() {
        return repository.countTarefasAtivasPorStatusDoUsuarioId(authenticatedUserProvider.currentUserId());
    }

    @Transactional(readOnly = true)
    public List<TarefaPorPrioridadeIndicadorDto> totalTarefasAtivasPorPrioridadeDoUsuarioLogado() {
        return repository.countTarefasAtivasPorPrioridadeDoUsuarioId(authenticatedUserProvider.currentUserId());
    }

    @Transactional(readOnly = true)
    public List<TarefaPorCategoriaIndicadorDto> totalTarefasAtivasPorCategoriaDoUsuarioLogado() {
        return repository.countTarefasAtivasPorCategoriaDoUsuarioId(authenticatedUserProvider.currentUserId());
    }

    @Transactional(readOnly = true)
    public List<TarefaPorCategoriaPrioridadeIndicadorDto> totalTarefasAtivasPorCategoriaEPrioridadeDoUsuarioLogado() {
        return repository.countTarefasAtivasPorCategoriaEPrioridadeDoUsuarioId(authenticatedUserProvider.currentUserId());
    }

    protected Specification<TarefaAnaliticoView> buildScopedSpecification(
            Specification<TarefaAnaliticoView> customSpecification
    ) {
        Long currentUserId = authenticatedUserProvider.currentUserId();
        Specification<TarefaAnaliticoView> usuarioLogadoSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(TarefaAnaliticoView_.USUARIO_ID), currentUserId);

        if (customSpecification == null) {
            return usuarioLogadoSpec;
        }

        return usuarioLogadoSpec.and(customSpecification);
    }
}
