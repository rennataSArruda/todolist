package br.com.rennataarruda.todolist.repository.view;

import br.com.rennataarruda.todolist.entity.view.TarefaAnaliticoView;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaAnaliticoViewRepository
        extends BaseRepository<TarefaAnaliticoView, Long>, TarefaAnaliticoViewRepositoryCustom {
}
