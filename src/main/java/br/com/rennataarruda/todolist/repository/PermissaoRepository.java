package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.fixed.Permissao;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissaoRepository extends BaseRepository<Permissao, Long> {

    Optional<Permissao> findByCodigo(String codigo);
}
