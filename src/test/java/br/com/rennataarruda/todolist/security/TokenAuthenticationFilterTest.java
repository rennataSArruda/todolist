package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.entity.Papel;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.Permissao;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private final UserAuthorityService userAuthorityService = new UserAuthorityService();
    private final SecurityExceptionHandler exceptionHandler = new SecurityExceptionHandler();

    @Test
    void shouldAuthenticateWithAuthoritiesFromProfilePermissions() throws ServletException, IOException {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter(
                jwtService,
                blacklistedTokenRepository,
                refreshTokenRepository,
                usuarioRepository,
                userAuthorityService,
                exceptionHandler
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/usuario");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Authentication> authenticationRef = new AtomicReference<>();

        Perfil perfil = new Perfil("PADRAO", "Perfil padrao");
        ReflectionTestUtils.setField(perfil, "id", 10L);
        perfil.adicionarAutorizacao(new Papel("USUARIO", "Usuarios"), new Permissao("VISUALIZAR", "Visualizar"));
        perfil.adicionarAutorizacao(new Papel("USUARIO", "Usuarios"), new Permissao("EDITAR", "Editar"));
        Usuario usuario = new Usuario("admin", "Administrador", "encoded-password", perfil);

        request.setServletPath("/api/usuario");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("access-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("access-token")).thenReturn(accessToken("jti-1", "admin", "session-1"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(
                eq("session-1"),
                any(LocalDateTime.class)
        )).thenReturn(true);
        when(usuarioRepository.findWithAuthorizationByUsername("admin"))
                .thenReturn(Optional.of(usuario));

        filter.doFilter(request, response, (req, res) ->
                authenticationRef.set(SecurityContextHolder.getContext().getAuthentication()));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(authenticationRef.get()).isNotNull();
        assertThat(authenticationRef.get().getPrincipal()).isInstanceOf(AuthenticatedUser.class);

        AuthenticatedUser principal = (AuthenticatedUser) authenticationRef.get().getPrincipal();
        assertThat(principal.username()).isEqualTo("admin");
        assertThat(principal.root()).isFalse();
        assertThat(principal.perfilId()).isEqualTo(10L);
        assertThat(principal.perfilCodigo()).isEqualTo("PADRAO");
        assertThat(authenticationRef.get().getAuthorities())
                .extracting(grantedAuthority -> grantedAuthority.getAuthority())
                .containsExactlyInAnyOrder("USUARIO_VISUALIZAR", "USUARIO_EDITAR");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldAuthenticateRootUserWithRootAuthority() throws ServletException, IOException {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter(
                jwtService,
                blacklistedTokenRepository,
                refreshTokenRepository,
                usuarioRepository,
                userAuthorityService,
                exceptionHandler
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/usuario");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Authentication> authenticationRef = new AtomicReference<>();

        Perfil perfil = new Perfil("PADRAO", "Perfil padrao");
        ReflectionTestUtils.setField(perfil, "id", 1L);
        Usuario usuario = new Usuario("root", "ROOT", "encoded-password", true, perfil);

        request.setServletPath("/api/usuario");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("access-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("access-token")).thenReturn(accessToken("jti-2", "root", "session-root"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(
                eq("session-root"),
                any(LocalDateTime.class)
        )).thenReturn(true);
        when(usuarioRepository.findWithAuthorizationByUsername("root"))
                .thenReturn(Optional.of(usuario));

        filter.doFilter(request, response, (req, res) ->
                authenticationRef.set(SecurityContextHolder.getContext().getAuthentication()));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(authenticationRef.get().getAuthorities())
                .extracting(grantedAuthority -> grantedAuthority.getAuthority())
                .containsExactly("ROOT");

        AuthenticatedUser principal = (AuthenticatedUser) authenticationRef.get().getPrincipal();
        assertThat(principal.root()).isTrue();
        assertThat(principal.perfilCodigo()).isEqualTo("PADRAO");
    }

    @Test
    void shouldReturnUnauthorizedWhenSessionIsInvalid() throws ServletException, IOException {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter(
                jwtService,
                blacklistedTokenRepository,
                refreshTokenRepository,
                usuarioRepository,
                userAuthorityService,
                exceptionHandler
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/usuario");
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setServletPath("/api/usuario");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("access-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("access-token")).thenReturn(accessToken("jti-1", "admin", "session-1"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(
                eq("session-1"),
                any(LocalDateTime.class)
        )).thenReturn(false);

        filter.doFilter(request, response, (req, res) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"status\":401");
        assertThat(response.getContentAsString()).contains("\"action\":\"LOGIN\"");
        assertThat(response.getContentAsString()).contains("AUTH_SESSION_INVALID");
        assertThat(response.getContentAsString()).contains("Sessao invalida");
        verify(usuarioRepository, never()).findWithAuthorizationByUsername("admin");
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsExpired() throws ServletException, IOException {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter(
                jwtService,
                blacklistedTokenRepository,
                refreshTokenRepository,
                usuarioRepository,
                userAuthorityService,
                exceptionHandler
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/usuario");
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setServletPath("/api/usuario");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer expired-token");
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("expired-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("expired-token"))
                .thenThrow(new ExpiredJwtException(null, null, "Token expirado"));

        filter.doFilter(request, response, (req, res) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"status\":401");
        assertThat(response.getContentAsString()).contains("\"action\":\"TOKEN_REFRESH\"");
        assertThat(response.getContentAsString()).contains("AUTH_TOKEN_EXPIRED");
        assertThat(response.getContentAsString()).contains("Token expirado");
        verify(refreshTokenRepository, never())
                .existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(any(), any(LocalDateTime.class));
        verify(usuarioRepository, never()).findWithAuthorizationByUsername(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsBlacklisted() throws ServletException, IOException {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter(
                jwtService,
                blacklistedTokenRepository,
                refreshTokenRepository,
                usuarioRepository,
                userAuthorityService,
                exceptionHandler
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/usuario");
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setServletPath("/api/usuario");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer blacklisted-token");
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("blacklisted-token"))).thenReturn(true);

        filter.doFilter(request, response, (req, res) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"status\":401");
        assertThat(response.getContentAsString()).contains("\"action\":\"LOGIN\"");
        assertThat(response.getContentAsString()).contains("AUTH_TOKEN_INVALIDATED");
        assertThat(response.getContentAsString()).contains("Token invalidado");
        verify(jwtService, never()).parseAccessToken(any());
        verify(usuarioRepository, never()).findWithAuthorizationByUsername(any());
    }

    private JwtService.AccessTokenClaims accessToken(String tokenId, String username, String sessionId) {
        return new JwtService.AccessTokenClaims(tokenId, username, sessionId, LocalDateTime.now().plusMinutes(15));
    }
}
