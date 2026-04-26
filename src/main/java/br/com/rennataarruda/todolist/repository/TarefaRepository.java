package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.Tarefa;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends BaseRepository<Tarefa, Long> {
}
