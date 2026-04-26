package br.com.rennataarruda.todolist.repository.fixed;

import br.com.rennataarruda.todolist.entity.fixed.TarefaStatus;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaStatusRepository extends BaseRepository<TarefaStatus, Long> {
}
