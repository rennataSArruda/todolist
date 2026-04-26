package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.entity.fixed.Papel;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.fixed.Permissao;
import br.com.rennataarruda.todolist.entity.Usuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthorityServiceTest {

    private final UserAuthorityService service = new UserAuthorityService();

    @Test
    void shouldReturnRootAuthorityForRootUser() {
        Usuario usuario = new Usuario("root", "ROOT", "encoded", true, new Perfil("PADRAO", "Perfil padrao"));

        assertThat(service.getAuthorities(usuario))
                .extracting(grantedAuthority -> grantedAuthority.getAuthority())
                .containsExactly("ROOT");
    }

    @Test
    void shouldComposeAuthoritiesFromPapelAndPermissao() {
        Perfil perfil = new Perfil("PADRAO", "Perfil padrao");
        perfil.adicionarAutorizacao(new Papel("USUARIO", "Usuarios"), new Permissao(1L, "VISUALIZAR", "Visualizar"));
        perfil.adicionarAutorizacao(new Papel("TAREFA", "Tarefas"), new Permissao(2L, "EDITAR", "Editar"));
        Usuario usuario = new Usuario("user", "User", "encoded", perfil);

        assertThat(service.getAuthorities(usuario))
                .extracting(grantedAuthority -> grantedAuthority.getAuthority())
                .containsExactlyInAnyOrder("USUARIO_VISUALIZAR", "TAREFA_EDITAR");
    }
}
