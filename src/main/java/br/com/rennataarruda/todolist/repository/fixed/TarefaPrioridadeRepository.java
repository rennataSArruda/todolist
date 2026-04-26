package br.com.rennataarruda.todolist.repository.fixed;

import br.com.rennataarruda.todolist.entity.fixed.TarefaPrioridade;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaPrioridadeRepository extends BaseRepository<TarefaPrioridade, Long> {
}
