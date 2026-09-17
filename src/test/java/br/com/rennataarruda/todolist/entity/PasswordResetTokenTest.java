package br.com.rennataarruda.todolist.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetTokenTest {

    @Test
    void shouldMarkTokenAsUsed() {
        PasswordResetToken token = new PasswordResetToken(
                "token-hash",
                usuario(),
                LocalDateTime.now().plusMinutes(15)
        );

        token.markAsUsed();

        assertThat(token.isUsed()).isTrue();
        assertThat(token.getUsedAt()).isNotNull();
    }

    @Test
    void shouldIdentifyExpiredToken() {
        PasswordResetToken token = new PasswordResetToken(
                "token-hash",
                usuario(),
                LocalDateTime.now().minusMinutes(1)
        );

        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void shouldIdentifyValidUnusedToken() {
        PasswordResetToken token = new PasswordResetToken(
                "token-hash",
                usuario(),
                LocalDateTime.now().plusMinutes(15)
        );

        assertThat(token.isUsed()).isFalse();
        assertThat(token.isExpired()).isFalse();
    }

    private Usuario usuario() {
        return new Usuario("admin", "admin@email.com", "Administrador", "encoded", new Perfil("PADRAO", "Perfil padrao"));
    }
}
