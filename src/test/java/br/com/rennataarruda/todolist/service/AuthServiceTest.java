package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.AuthRequest;
import br.com.rennataarruda.todolist.dto.auth.AuthResponse;
import br.com.rennataarruda.todolist.dto.auth.ChangePasswordRequest;
import br.com.rennataarruda.todolist.dto.auth.RefreshTokenRequest;
import br.com.rennataarruda.todolist.entity.BlacklistedToken;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.RefreshToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.JwtProperties;
import br.com.rennataarruda.todolist.security.JwtService;
import br.com.rennataarruda.todolist.security.PasswordService;
import br.com.rennataarruda.todolist.security.SecurityUtils;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordService passwordService;

    private AuthService service;

    private final JwtProperties jwtProperties = new JwtProperties(
            "12345678901234567890123456789012",
            15,
            7,
            1,
            "todolist-api",
            "todolist-api"
    );

    @BeforeEach
    void setUp() {
        service = new AuthService(
                usuarioRepository,
                blacklistedTokenRepository,
                refreshTokenRepository,
                jwtService,
                jwtProperties,
                passwordService
        );
    }

    @Test
    void shouldLoginWithValidCredentials() {
        Usuario usuario = usuarioComSenha("admin", "senha123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordService.matches("senha123", "hash-senha123")).thenReturn(true);
        when(refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(jwtService.generateAccessToken(eq("admin"), anyString())).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = service.login(new AuthRequest("admin", "senha123"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900);
    }

    @Test
    void shouldRejectLoginWithInvalidPassword() {
        Usuario usuario = usuarioComSenha("admin", "senha123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordService.matches("senhaErrada", "hash-senha123")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new AuthRequest("admin", "senhaErrada")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais invalidas");
    }

    @Test
    void shouldRejectLoginWithBlankUsername() {
        assertThatThrownBy(() -> service.login(new AuthRequest("", "senha123")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username e password sao obrigatorios");

        verify(usuarioRepository, never()).findByUsername(anyString());
    }

    @Test
    void shouldPersistHashedRefreshTokenOnlyOnLogin() {
        Usuario usuario = usuarioComSenha("admin", "senha123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordService.matches("senha123", "hash-senha123")).thenReturn(true);
        when(refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(jwtService.generateAccessToken(eq("admin"), anyString())).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = service.login(new AuthRequest("admin", "senha123"));

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getTokenHash()).isEqualTo(SecurityUtils.hashSHA256(response.refreshToken()));
        assertThat(captor.getValue().getTokenHash()).isNotEqualTo(response.refreshToken());
    }

    @Test
    void shouldRefreshWithValidRefreshToken() {
        Usuario usuario = usuarioComSenha("admin", "senha123");
        RefreshToken refreshToken = new RefreshToken(
                SecurityUtils.hashSHA256("refresh-token"),
                "session-1",
                usuario,
                LocalDateTime.now().plusDays(1)
        );

        when(refreshTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("refresh-token"))).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(jwtService.generateAccessToken(eq("admin"), anyString())).thenReturn("new-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = service.refresh(new RefreshTokenRequest("refresh-token"));

        assertThat(refreshToken.isRevoked()).isTrue();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void shouldRejectRefreshWithInvalidRefreshToken() {
        when(refreshTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("refresh-token"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh(new RefreshTokenRequest("refresh-token")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Refresh token invalido");
    }

    @Test
    void shouldRejectRefreshWithRevokedRefreshToken() {
        Usuario usuario = usuarioComSenha("admin", "senha123");
        RefreshToken refreshToken = new RefreshToken(
                SecurityUtils.hashSHA256("refresh-token"),
                "session-1",
                usuario,
                LocalDateTime.now().plusDays(1)
        );
        refreshToken.revoke();

        when(refreshTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("refresh-token"))).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> service.refresh(new RefreshTokenRequest("refresh-token")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Refresh token invalido");

        verify(jwtService, never()).generateAccessToken(anyString(), anyString());
    }

    @Test
    void shouldLogoutRevokingRefreshTokenAndBlacklistingAccessTokenHash() {
        Usuario usuario = usuarioComSenha("admin", "senha123");
        RefreshToken refreshToken = new RefreshToken(
                SecurityUtils.hashSHA256("refresh-token"),
                "session-1",
                usuario,
                LocalDateTime.now().plusDays(1)
        );
        JwtService.AccessTokenClaims accessTokenClaims = new JwtService.AccessTokenClaims(
                "jti-1",
                "admin",
                "session-1",
                LocalDateTime.now().plusMinutes(15)
        );

        when(refreshTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("refresh-token"))).thenReturn(Optional.of(refreshToken));
        when(jwtService.parseAccessToken("access-token")).thenReturn(accessTokenClaims);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("access-token"))).thenReturn(false);

        service.logout("Bearer access-token", new RefreshTokenRequest("refresh-token"));

        assertThat(refreshToken.isRevoked()).isTrue();
        ArgumentCaptor<BlacklistedToken> captor = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(blacklistedTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getTokenHash()).isEqualTo(SecurityUtils.hashSHA256("access-token"));
    }

    @Test
    void shouldRejectLogoutWhenAccessTokenSessionDoesNotMatchRefreshTokenSession() {
        Usuario usuario = usuarioComSenha("admin", "senha123");
        RefreshToken refreshToken = new RefreshToken(
                SecurityUtils.hashSHA256("refresh-token"),
                "session-refresh",
                usuario,
                LocalDateTime.now().plusDays(1)
        );
        JwtService.AccessTokenClaims accessTokenClaims = new JwtService.AccessTokenClaims(
                "jti-1",
                "admin",
                "session-access",
                LocalDateTime.now().plusMinutes(15)
        );

        when(refreshTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("refresh-token"))).thenReturn(Optional.of(refreshToken));
        when(jwtService.parseAccessToken("access-token")).thenReturn(accessTokenClaims);

        assertThatThrownBy(() -> service.logout("Bearer access-token", new RefreshTokenRequest("refresh-token")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Sessao invalida");

        assertThat(refreshToken.isRevoked()).isFalse();
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        verify(blacklistedTokenRepository, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void shouldRejectLogoutWhenAccessTokenIsInvalid() {
        Usuario usuario = usuarioComSenha("admin", "senha123");
        RefreshToken refreshToken = new RefreshToken(
                SecurityUtils.hashSHA256("refresh-token"),
                "session-1",
                usuario,
                LocalDateTime.now().plusDays(1)
        );

        when(refreshTokenRepository.findByTokenHash(SecurityUtils.hashSHA256("refresh-token"))).thenReturn(Optional.of(refreshToken));
        when(jwtService.parseAccessToken("access-token")).thenThrow(new JwtException("Token invalido"));

        assertThatThrownBy(() -> service.logout("Bearer access-token", new RefreshTokenRequest("refresh-token")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Token invalido");

        assertThat(refreshToken.isRevoked()).isFalse();
        verify(blacklistedTokenRepository, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void shouldChangePasswordAndRevokeAllActiveSessions() {
        Usuario usuario = usuarioComSenha("admin", "senhaAtual123");
        RefreshToken oldestSession = new RefreshToken(
                SecurityUtils.hashSHA256("refresh-1"),
                "session-1",
                usuario,
                LocalDateTime.now().plusDays(1)
        );
        RefreshToken newestSession = new RefreshToken(
                SecurityUtils.hashSHA256("refresh-2"),
                "session-2",
                usuario,
                LocalDateTime.now().plusDays(1)
        );

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordService.matches("senhaAtual123", "hash-senhaAtual123")).thenReturn(true);
        when(passwordService.matches("novaSenha123", "hash-senhaAtual123")).thenReturn(false);
        when(passwordService.encode("novaSenha123")).thenReturn("hash-novaSenha123");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(java.util.List.of(oldestSession, newestSession));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.changePassword("admin", new ChangePasswordRequest("senhaAtual123", "novaSenha123"));

        assertThat(usuario.getPassword()).isEqualTo("hash-novaSenha123");
        assertThat(oldestSession.isRevoked()).isTrue();
        assertThat(newestSession.isRevoked()).isTrue();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void shouldRejectPasswordChangeWithInvalidCurrentPassword() {
        Usuario usuario = usuarioComSenha("admin", "senhaAtual123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordService.matches("senhaErrada", "hash-senhaAtual123")).thenReturn(false);

        assertThatThrownBy(() -> service.changePassword("admin", new ChangePasswordRequest("senhaErrada", "novaSenha123")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Senha atual invalida");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldRejectPasswordChangeWithWeakNewPassword() {
        assertThatThrownBy(() -> service.changePassword("admin", new ChangePasswordRequest("senhaAtual123", "123")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Nova senha deve ter ao menos 8 caracteres");

        verify(usuarioRepository, never()).findByUsername(anyString());
    }

    private Usuario usuarioComSenha(String username, String passwordEmTextoPlano) {
        return new Usuario(username, "Administrador", "hash-" + passwordEmTextoPlano, new Perfil("PADRAO", "Perfil padrao"));
    }
}
