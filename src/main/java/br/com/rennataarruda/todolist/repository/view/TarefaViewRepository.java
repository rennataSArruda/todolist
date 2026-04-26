package br.com.rennataarruda.todolist.repository.view;

import br.com.rennataarruda.todolist.entity.view.TarefaView;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaViewRepository extends BaseRepository<TarefaView, Long> {
}
