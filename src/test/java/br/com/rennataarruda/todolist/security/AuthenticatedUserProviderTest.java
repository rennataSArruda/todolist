package br.com.rennataarruda.todolist.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticatedUserProviderTest {

    private final AuthenticatedUserProvider provider = new AuthenticatedUserProvider();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentAuthenticatedUser() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                10L,
                "admin",
                "Administrador",
                true,
                1L,
                "ROOT",
                "session-1"
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of())
        );

        assertThat(provider.currentUser()).isEqualTo(authenticatedUser);
        assertThat(provider.currentUserId()).isEqualTo(10L);
    }

    @Test
    void shouldRejectMissingAuthenticatedUser() {
        assertThatThrownBy(provider::currentUser)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario autenticado nao encontrado");
    }

    @Test
    void shouldRejectUnexpectedPrincipalType() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, List.of())
        );

        assertThatThrownBy(provider::currentUser)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario autenticado nao encontrado");
    }
}
