package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.entity.fixed.enumerations.PapelCodigo;
import br.com.rennataarruda.todolist.entity.fixed.enumerations.PermissaoCodigo;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationAccessServiceTest {

    private final AuthorizationAccessService service = new AuthorizationAccessService();

    @Test
    void shouldGrantAccessWhenUserHasRequestedAuthority() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user",
                null,
                List.of(new SimpleGrantedAuthority("USUARIO_VISUALIZAR"))
        );

        assertThat(service.hasResourcePermission(authentication, PapelCodigo.USUARIO, PermissaoCodigo.VISUALIZAR)).isTrue();
    }

    @Test
    void shouldGrantAccessWhenUserIsRoot() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "root",
                null,
                List.of(new SimpleGrantedAuthority("ROOT"))
        );

        assertThat(service.hasResourcePermission(authentication, PapelCodigo.USUARIO, PermissaoCodigo.EDITAR)).isTrue();
    }

    @Test
    void shouldRecognizeRootAccess() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "root",
                null,
                List.of(new SimpleGrantedAuthority("ROOT"))
        );

        assertThat(service.hasRoot(authentication)).isTrue();
    }

    @Test
    void shouldDenyAccessWhenAuthorityIsMissing() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user",
                null,
                List.of(new SimpleGrantedAuthority("USUARIO_VISUALIZAR"))
        );

        assertThat(service.hasResourcePermission(authentication, PapelCodigo.USUARIO, PermissaoCodigo.EDITAR)).isFalse();
    }
}
