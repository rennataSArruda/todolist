package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.fixed.Papel;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PapelRepository extends BaseRepository<Papel, Long> {

    @Override
    @EntityGraph(attributePaths = "permissoes.permissao")
    List<Papel> findAll();

    @Override
    @EntityGraph(attributePaths = "permissoes.permissao")
    Optional<Papel> findById(Long id);

    @EntityGraph(attributePaths = "permissoes.permissao")
    Optional<Papel> findByCodigo(String codigo);
}
