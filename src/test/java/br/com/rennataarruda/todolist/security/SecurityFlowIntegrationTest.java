package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.dto.auth.AuthRequest;
import br.com.rennataarruda.todolist.dto.auth.AuthResponse;
import br.com.rennataarruda.todolist.dto.auth.ChangePasswordRequest;
import br.com.rennataarruda.todolist.dto.auth.RefreshTokenRequest;
import br.com.rennataarruda.todolist.entity.Papel;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.Permissao;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.service.AuthService;
import br.com.rennataarruda.todolist.service.UsuarioService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
        "app.security.jwt.secret=12345678901234567890123456789012"
})
@AutoConfigureMockMvc
class SecurityFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PerfilRepository perfilRepository;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private AuthService authService;

    @Test
    void shouldRejectAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/usuario").servletPath("/api/usuario"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUTH_TOKEN_MISSING"))
                .andExpect(jsonPath("$.action").value("LOGIN"));

        verify(usuarioRepository, never()).findWithAuthorizationByUsername(any());
    }

    @Test
    void shouldAllowAccessWithValidTokenAndPermission() throws Exception {
        Usuario usuario = usuarioComPermissao("admin", "USUARIO", "VISUALIZAR");

        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("valid-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("valid-token")).thenReturn(accessToken("jti-1", "admin", "session-1"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(eq("session-1"), any(LocalDateTime.class)))
                .thenReturn(true);
        when(usuarioRepository.findWithAuthorizationByUsername("admin")).thenReturn(Optional.of(usuario));
        when(usuarioService.get()).thenReturn(List.of(new UsuarioDto(1L, "admin", "Administrador", null)));

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAuthenticatedUserDataForCurrentSession() throws Exception {
        Usuario usuario = usuarioComPermissao("admin", "USUARIO", "VISUALIZAR");

        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("valid-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("valid-token")).thenReturn(accessToken("jti-me", "admin", "session-me"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(eq("session-me"), any(LocalDateTime.class)))
                .thenReturn(true);
        when(usuarioRepository.findWithAuthorizationByUsername("admin")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/auth/me")
                        .servletPath("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.nome").value("Administrador"))
                .andExpect(jsonPath("$.perfil.id").value(10))
                .andExpect(jsonPath("$.perfil.codigo").value("PADRAO"))
                .andExpect(jsonPath("$.authorities[0]").value("USUARIO_VISUALIZAR"))
                .andExpect(jsonPath("$.root").value(false));
    }

    @Test
    void shouldChangePasswordThroughProtectedEndpoint() throws Exception {
        Usuario usuario = usuarioComPermissao("admin", "USUARIO", "VISUALIZAR");

        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("valid-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("valid-token")).thenReturn(accessToken("jti-password", "admin", "session-password"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(eq("session-password"), any(LocalDateTime.class)))
                .thenReturn(true);
        when(usuarioRepository.findWithAuthorizationByUsername("admin")).thenReturn(Optional.of(usuario));

        mockMvc.perform(put("/api/auth/password")
                        .servletPath("/api/auth/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"senhaAtual123\",\"newPassword\":\"novaSenha123\"}"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(authService).changePassword("admin", new ChangePasswordRequest("senhaAtual123", "novaSenha123"));
    }

    @Test
    void shouldRejectAccessWithInvalidToken() throws Exception {
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("invalid-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("invalid-token")).thenThrow(new JwtException("Token invalido"));

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUTH_TOKEN_INVALID"))
                .andExpect(jsonPath("$.action").value("LOGIN"));
    }

    @Test
    void shouldRejectAccessWithExpiredToken() throws Exception {
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("expired-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("expired-token"))
                .thenThrow(new ExpiredJwtException(null, null, "Token expirado"));

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer expired-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUTH_TOKEN_EXPIRED"))
                .andExpect(jsonPath("$.action").value("TOKEN_REFRESH"));
    }

    @Test
    void shouldRejectAccessWithBlacklistedToken() throws Exception {
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("blacklisted-token"))).thenReturn(true);

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer blacklisted-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUTH_TOKEN_INVALIDATED"))
                .andExpect(jsonPath("$.action").value("LOGIN"));
    }

    @Test
    void shouldRejectAccessWhenSessionIsRevoked() throws Exception {
        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("valid-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("valid-token")).thenReturn(accessToken("jti-1", "admin", "session-revoked"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(eq("session-revoked"), any(LocalDateTime.class)))
                .thenReturn(false);

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUTH_SESSION_INVALID"))
                .andExpect(jsonPath("$.action").value("LOGIN"));
    }

    @Test
    void shouldRejectAccessWhenUserHasNoPermission() throws Exception {
        Usuario usuario = new Usuario("user", "Usuario", "encoded", new Perfil("PADRAO", "Perfil padrao"));
        ReflectionTestUtils.setField(usuario, "perfil", null);

        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("valid-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("valid-token")).thenReturn(accessToken("jti-2", "user", "session-2"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(eq("session-2"), any(LocalDateTime.class)))
                .thenReturn(true);
        when(usuarioRepository.findWithAuthorizationByUsername("user")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("AUTH_USER_WITHOUT_PROFILE"))
                .andExpect(jsonPath("$.action").value("FORBIDDEN"));
    }

    @Test
    void shouldRejectAccessWhenUserLacksRequiredPermission() throws Exception {
        Usuario usuario = usuarioComPermissao("user", "USUARIO", "CRIAR");

        when(blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256("valid-token"))).thenReturn(false);
        when(jwtService.parseAccessToken("valid-token")).thenReturn(accessToken("jti-3", "user", "session-3"));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(eq("session-3"), any(LocalDateTime.class)))
                .thenReturn(true);
        when(usuarioRepository.findWithAuthorizationByUsername("user")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("AUTH_ACCESS_DENIED"))
                .andExpect(jsonPath("$.action").value("FORBIDDEN"));
    }

    @Test
    void shouldLoginThroughHttpEndpoint() throws Exception {
        AuthResponse response = new AuthResponse("access-token", "refresh-token", "Bearer", 900);
        when(authService.login(new AuthRequest("admin", "senha123"))).thenReturn(response);

        mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));

        verify(authService).login(new AuthRequest("admin", "senha123"));
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginIsInvalid() throws Exception {
        when(authService.login(new AuthRequest("admin", "senhaErrada")))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Credenciais invalidas"));

        mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"senhaErrada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRefreshThroughHttpEndpoint() throws Exception {
        AuthResponse response = new AuthResponse("new-access-token", "new-refresh-token", "Bearer", 900);
        when(authService.refresh(new RefreshTokenRequest("refresh-token"))).thenReturn(response);

        mockMvc.perform(post("/public/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));

        verify(authService).refresh(new RefreshTokenRequest("refresh-token"));
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenIsInvalidThroughHttpEndpoint() throws Exception {
        when(authService.refresh(new RefreshTokenRequest("refresh-token")))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Refresh token invalido"));

        mockMvc.perform(post("/public/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLogoutThroughHttpEndpoint() throws Exception {
        mockMvc.perform(post("/public/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(authService).logout("Bearer access-token", new RefreshTokenRequest("refresh-token"));
    }

    private JwtService.AccessTokenClaims accessToken(String tokenId, String username, String sessionId) {
        return new JwtService.AccessTokenClaims(tokenId, username, sessionId, LocalDateTime.now().plusMinutes(15));
    }

    private Usuario usuarioComPermissao(String username, String papelCodigo, String permissaoCodigo) {
        Perfil perfil = new Perfil("PADRAO", "Perfil padrao");
        ReflectionTestUtils.setField(perfil, "id", 10L);
        perfil.adicionarAutorizacao(
                new Papel(papelCodigo, "Papel " + papelCodigo),
                new Permissao(permissaoCodigo, "Permissao " + permissaoCodigo)
        );
        Usuario usuario = new Usuario(username, "Administrador", "encoded", perfil);
        ReflectionTestUtils.setField(usuario, "id", 1L);
        return usuario;
    }
}
