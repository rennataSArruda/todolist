package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.ForgotPasswordRequest;
import br.com.rennataarruda.todolist.dto.auth.ResetPasswordRequest;
import br.com.rennataarruda.todolist.entity.PasswordResetToken;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.RefreshToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.PasswordResetTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.PasswordService;
import br.com.rennataarruda.todolist.security.SecurityUtils;
import br.com.rennataarruda.todolist.service.email.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private MailService mailService;

    private PasswordResetService service;

    @BeforeEach
    void setUp() {
        service = new TestablePasswordResetService(
                usuarioRepository,
                passwordResetTokenRepository,
                refreshTokenRepository,
                passwordService,
                mailService
        );
    }

    @Test
    void shouldCreateResetTokenByUsernameAndSendEmail() {
        Usuario usuario = usuario();

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordResetTokenRepository.findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mailService.sendPasswordReset("admin@email.com", "https://app/reset?token=raw-token")).thenReturn(true);

        service.forgotPassword(new ForgotPasswordRequest("admin"));

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getTokenHash()).isEqualTo(SecurityUtils.hashSHA256("raw-token"));
        assertThat(captor.getValue().getTokenHash()).isNotEqualTo("raw-token");
        assertThat(captor.getValue().getUsuario()).isSameAs(usuario);
        assertThat(captor.getValue().isUsed()).isFalse();
        verify(mailService).sendPasswordReset("admin@email.com", "https://app/reset?token=raw-token");
    }

    @Test
    void shouldCreateResetTokenByEmail() {
        Usuario usuario = usuario();

        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(usuario));
        when(passwordResetTokenRepository.findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mailService.sendPasswordReset("admin@email.com", "https://app/reset?token=raw-token")).thenReturn(true);

        service.forgotPassword(new ForgotPasswordRequest("admin@email.com"));

        verify(usuarioRepository).findByEmail("admin@email.com");
        verify(mailService).sendPasswordReset("admin@email.com", "https://app/reset?token=raw-token");
    }

    @Test
    void shouldNotRevealUnknownIdentifier() {
        when(usuarioRepository.findByUsername("desconhecido")).thenReturn(Optional.empty());

        service.forgotPassword(new ForgotPasswordRequest("desconhecido"));

        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailService, never()).sendPasswordReset(anyString(), anyString());
    }

    @Test
    void shouldIgnoreInactiveUserOnForgotPassword() {
        Usuario usuario = usuario();
        usuario.alternarAtivo();

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        service.forgotPassword(new ForgotPasswordRequest("admin"));

        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailService, never()).sendPasswordReset(anyString(), anyString());
    }

    @Test
    void shouldInvalidatePreviousActiveTokensBeforeCreatingANewOne() {
        Usuario usuario = usuario();
        PasswordResetToken oldToken = new PasswordResetToken(
                "old-token-hash",
                usuario,
                LocalDateTime.now().plusMinutes(10)
        );

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordResetTokenRepository.findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(List.of(oldToken));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mailService.sendPasswordReset("admin@email.com", "https://app/reset?token=raw-token")).thenReturn(true);

        service.forgotPassword(new ForgotPasswordRequest("admin"));

        assertThat(oldToken.isUsed()).isTrue();
        verify(passwordResetTokenRepository).save(oldToken);
    }

    @Test
    void shouldMarkNewTokenAsUsedWhenEmailIsNotSent() {
        Usuario usuario = usuario();

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordResetTokenRepository.findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mailService.sendPasswordReset("admin@email.com", "https://app/reset?token=raw-token")).thenReturn(false);

        service.forgotPassword(new ForgotPasswordRequest("admin"));

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).isUsed()).isTrue();
    }

    @Test
    void shouldResetPasswordWithValidTokenAndRevokeActiveSessions() {
        Usuario usuario = usuario();
        PasswordResetToken resetToken = new PasswordResetToken(
                SecurityUtils.hashSHA256("raw-token"),
                usuario,
                LocalDateTime.now().plusMinutes(10)
        );
        RefreshToken refreshToken = new RefreshToken(
                "refresh-hash",
                "session-1",
                usuario,
                LocalDateTime.now().plusDays(1)
        );

        when(passwordResetTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("raw-token"))).thenReturn(Optional.of(resetToken));
        when(passwordService.encode("novaSenha123")).thenReturn("encoded-new-password");
        when(passwordResetTokenRepository.save(resetToken)).thenReturn(resetToken);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(List.of(refreshToken));
        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        service.resetPassword(new ResetPasswordRequest("raw-token", "novaSenha123"));

        assertThat(usuario.getPassword()).isEqualTo("encoded-new-password");
        assertThat(resetToken.isUsed()).isTrue();
        assertThat(refreshToken.isRevoked()).isTrue();
        verify(usuarioRepository).save(usuario);
        verify(passwordResetTokenRepository).save(resetToken);
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void shouldRejectUsedToken() {
        PasswordResetToken resetToken = new PasswordResetToken(
                SecurityUtils.hashSHA256("raw-token"),
                usuario(),
                LocalDateTime.now().plusMinutes(10)
        );
        resetToken.markAsUsed();

        when(passwordResetTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("raw-token"))).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() -> service.resetPassword(new ResetPasswordRequest("raw-token", "novaSenha123")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Token de recuperação inválido");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldRejectExpiredToken() {
        PasswordResetToken resetToken = new PasswordResetToken(
                SecurityUtils.hashSHA256("raw-token"),
                usuario(),
                LocalDateTime.now().minusMinutes(1)
        );

        when(passwordResetTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("raw-token"))).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() -> service.resetPassword(new ResetPasswordRequest("raw-token", "novaSenha123")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Token de recuperação inválido");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldRejectWeakNewPassword() {
        assertThatThrownBy(() -> service.resetPassword(new ResetPasswordRequest("raw-token", "123")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Nova senha deve ter pelo menos 8 caracteres");

        verify(passwordResetTokenRepository, never()).findByTokenHash(anyString());
    }

    private Usuario usuario() {
        return new Usuario("admin", "admin@email.com", "Administrador", "encoded-password", new Perfil("PADRAO", "Perfil padrao"));
    }

    private static class TestablePasswordResetService extends PasswordResetService {

        private TestablePasswordResetService(
                UsuarioRepository usuarioRepository,
                PasswordResetTokenRepository passwordResetTokenRepository,
                RefreshTokenRepository refreshTokenRepository,
                PasswordService passwordService,
                MailService mailService
        ) {
            super(
                    usuarioRepository,
                    passwordResetTokenRepository,
                    refreshTokenRepository,
                    passwordService,
                    mailService,
                    "https://app/reset?token={token}"
            );
        }

        @Override
        protected String generateToken() {
            return "raw-token";
        }
    }
}