package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    @EntityGraph(attributePaths = {
            "perfil",
            "perfil.autorizacoes",
            "perfil.autorizacoes.papel",
            "perfil.autorizacoes.permissao"
    })
    Optional<Usuario> findWithAuthorizationByUsername(String username);
}
