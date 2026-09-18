package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.ConfiguracoesGerais;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracoesGeraisRepository extends BaseRepository<ConfiguracoesGerais, Long> {

    Optional<ConfiguracoesGerais> findFirstByOrderByIdAsc();
}
