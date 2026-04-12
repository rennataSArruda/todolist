package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends BaseRepository<Perfil, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    Optional<Perfil> findByCodigo(String codigo);
}
