package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    Optional<Usuario> findByUsername(String username);
}
