package br.com.rennataarruda.todolist.repository.view.implementation;

import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorCategoriaPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorPrioridadeIndicadorDto;
import br.com.rennataarruda.todolist.dto.indicador.TarefaPorStatusIndicadorDto;
import br.com.rennataarruda.todolist.entity.view.TarefaAnaliticoView;
import br.com.rennataarruda.todolist.entity.view.TarefaAnaliticoView_;
import br.com.rennataarruda.todolist.repository.view.TarefaAnaliticoViewRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TarefaAnaliticoViewRepositoryImpl implements TarefaAnaliticoViewRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TarefaPorStatusIndicadorDto> countTarefasAtivasPorStatusDoUsuarioId(Long usuarioId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TarefaPorStatusIndicadorDto> cq = cb.createQuery(TarefaPorStatusIndicadorDto.class);
        Root<TarefaAnaliticoView> root = cq.from(TarefaAnaliticoView.class);
        Expression<Long> totalExpression = cb.count(root);

        Predicate usuarioPredicate = cb.equal(root.get(TarefaAnaliticoView_.USUARIO_ID), usuarioId);
        Predicate ativoPredicate = cb.equal(root.get(TarefaAnaliticoView_.ATIVO), true);

        cq.select(cb.construct(
                TarefaPorStatusIndicadorDto.class,
                root.get(TarefaAnaliticoView_.STATUS_ID),
                root.get(TarefaAnaliticoView_.STATUS_CODIGO),
                root.get(TarefaAnaliticoView_.STATUS_DESCRICAO),
                totalExpression
        ));

        cq.where(cb.and(usuarioPredicate, ativoPredicate));
        cq.groupBy(
                root.get(TarefaAnaliticoView_.STATUS_ID),
                root.get(TarefaAnaliticoView_.STATUS_CODIGO),
                root.get(TarefaAnaliticoView_.STATUS_DESCRICAO)
        );
        cq.orderBy(cb.desc(totalExpression));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<TarefaPorPrioridadeIndicadorDto> countTarefasAtivasPorPrioridadeDoUsuarioId(Long usuarioId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TarefaPorPrioridadeIndicadorDto> cq = cb.createQuery(TarefaPorPrioridadeIndicadorDto.class);
        Root<TarefaAnaliticoView> root = cq.from(TarefaAnaliticoView.class);
        Expression<Long> totalExpression = cb.count(root);

        Predicate usuarioPredicate = cb.equal(root.get(TarefaAnaliticoView_.USUARIO_ID), usuarioId);
        Predicate ativoPredicate = cb.equal(root.get(TarefaAnaliticoView_.ATIVO), true);

        cq.select(cb.construct(
                TarefaPorPrioridadeIndicadorDto.class,
                root.get(TarefaAnaliticoView_.PRIORIDADE_ID),
                root.get(TarefaAnaliticoView_.PRIORIDADE_CODIGO),
                root.get(TarefaAnaliticoView_.PRIORIDADE_DESCRICAO),
                totalExpression
        ));

        cq.where(cb.and(usuarioPredicate, ativoPredicate));
        cq.groupBy(
                root.get(TarefaAnaliticoView_.PRIORIDADE_ID),
                root.get(TarefaAnaliticoView_.PRIORIDADE_CODIGO),
                root.get(TarefaAnaliticoView_.PRIORIDADE_DESCRICAO)
        );
        cq.orderBy(cb.desc(totalExpression));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<TarefaPorCategoriaIndicadorDto> countTarefasAtivasPorCategoriaDoUsuarioId(Long usuarioId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TarefaPorCategoriaIndicadorDto> cq = cb.createQuery(TarefaPorCategoriaIndicadorDto.class);
        Root<TarefaAnaliticoView> root = cq.from(TarefaAnaliticoView.class);
        Expression<Long> totalExpression = cb.count(root);

        Predicate usuarioPredicate = cb.equal(root.get(TarefaAnaliticoView_.USUARIO_ID), usuarioId);
        Predicate ativoPredicate = cb.equal(root.get(TarefaAnaliticoView_.ATIVO), true);

        cq.select(cb.construct(
                TarefaPorCategoriaIndicadorDto.class,
                root.get(TarefaAnaliticoView_.CATEGORIA_ID),
                root.get(TarefaAnaliticoView_.CATEGORIA_NOME),
                totalExpression
        ));

        cq.where(cb.and(usuarioPredicate, ativoPredicate));
        cq.groupBy(
                root.get(TarefaAnaliticoView_.CATEGORIA_ID),
                root.get(TarefaAnaliticoView_.CATEGORIA_NOME)
        );
        cq.orderBy(cb.desc(totalExpression));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<TarefaPorCategoriaPrioridadeIndicadorDto> countTarefasAtivasPorCategoriaEPrioridadeDoUsuarioId(Long usuarioId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TarefaPorCategoriaPrioridadeIndicadorDto> cq = cb.createQuery(TarefaPorCategoriaPrioridadeIndicadorDto.class);
        Root<TarefaAnaliticoView> root = cq.from(TarefaAnaliticoView.class);
        Expression<Long> totalExpression = cb.count(root);

        Predicate usuarioPredicate = cb.equal(root.get(TarefaAnaliticoView_.USUARIO_ID), usuarioId);
        Predicate ativoPredicate = cb.equal(root.get(TarefaAnaliticoView_.ATIVO), true);

        cq.select(cb.construct(
                TarefaPorCategoriaPrioridadeIndicadorDto.class,
                root.get(TarefaAnaliticoView_.CATEGORIA_ID),
                root.get(TarefaAnaliticoView_.CATEGORIA_NOME),
                root.get(TarefaAnaliticoView_.PRIORIDADE_ID),
                root.get(TarefaAnaliticoView_.PRIORIDADE_CODIGO),
                root.get(TarefaAnaliticoView_.PRIORIDADE_DESCRICAO),
                totalExpression
        ));

        cq.where(cb.and(usuarioPredicate, ativoPredicate));
        cq.groupBy(
                root.get(TarefaAnaliticoView_.CATEGORIA_ID),
                root.get(TarefaAnaliticoView_.CATEGORIA_NOME),
                root.get(TarefaAnaliticoView_.PRIORIDADE_ID),
                root.get(TarefaAnaliticoView_.PRIORIDADE_CODIGO),
                root.get(TarefaAnaliticoView_.PRIORIDADE_DESCRICAO)
        );
        cq.orderBy(
                cb.asc(root.get(TarefaAnaliticoView_.CATEGORIA_NOME)),
                cb.asc(root.get(TarefaAnaliticoView_.PRIORIDADE_ID))
        );

        return entityManager.createQuery(cq).getResultList();
    }
}
