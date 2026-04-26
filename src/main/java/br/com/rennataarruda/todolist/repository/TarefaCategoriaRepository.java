package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.TarefaCategoria;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaCategoriaRepository extends BaseRepository<TarefaCategoria, Long> {
}
