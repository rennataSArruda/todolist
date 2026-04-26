package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.entity.Papel;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.Permissao;
import br.com.rennataarruda.todolist.entity.RefreshToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.PapelPermissaoRepository;
import br.com.rennataarruda.todolist.repository.PapelRepository;
import br.com.rennataarruda.todolist.repository.PerfilPapelPermissaoRepository;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.repository.PermissaoRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.TarefaCategoriaRepository;
import br.com.rennataarruda.todolist.repository.TarefaRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.repository.fixed.TarefaPrioridadeRepository;
import br.com.rennataarruda.todolist.repository.fixed.TarefaStatusRepository;
import br.com.rennataarruda.todolist.repository.view.TarefaViewRepository;
import br.com.rennataarruda.todolist.service.UsuarioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class RealSecurityFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordService passwordService;

    @Autowired
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
    private PapelRepository papelRepository;

    @MockBean
    private PermissaoRepository permissaoRepository;

    @MockBean
    private PerfilPapelPermissaoRepository perfilPapelPermissaoRepository;

    @MockBean
    private PapelPermissaoRepository papelPermissaoRepository;

    @MockBean
    private TarefaCategoriaRepository tarefaCategoriaRepository;

    @MockBean
    private TarefaRepository tarefaRepository;

    @MockBean
    private TarefaViewRepository tarefaViewRepository;

    @MockBean
    private TarefaStatusRepository tarefaStatusRepository;

    @MockBean
    private TarefaPrioridadeRepository tarefaPrioridadeRepository;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void shouldLoginWithRealJwtAndAccessProtectedEndpoint() throws Exception {
        Usuario usuario = usuarioComPermissao("admin", "senha123", "USUARIO", "VISUALIZAR");
        AtomicReference<RefreshToken> savedRefreshToken = new AtomicReference<>();

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findWithAuthorizationByUsername("admin")).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken refreshToken = invocation.getArgument(0);
            savedRefreshToken.set(refreshToken);
            return refreshToken;
        });
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(blacklistedTokenRepository.existsByTokenHash(anyString())).thenReturn(false);
        when(usuarioService.get()).thenReturn(List.of(new UsuarioDto(1L, "admin", "Administrador", null)));

        MvcResult loginResult = mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        JsonNode authResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = authResponse.get("accessToken").asText();

        JwtService.AccessTokenClaims claims = jwtService.parseAccessToken(accessToken);
        assertThat(claims.username()).isEqualTo("admin");
        assertThat(claims.sessionId()).isNotBlank();

        mockMvc.perform(get("/api/usuario")
                        .servletPath("/api/usuario")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/me")
                        .servletPath("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.nome").value("Administrador"))
                .andExpect(jsonPath("$.perfil.id").value(10))
                .andExpect(jsonPath("$.perfil.codigo").value("PADRAO"))
                .andExpect(jsonPath("$.authorities[0]").value("USUARIO_VISUALIZAR"))
                .andExpect(jsonPath("$.root").value(false));

        String refreshToken = authResponse.get("refreshToken").asText();
        when(refreshTokenRepository.findByTokenHash(SecurityUtils.hashSHA256(refreshToken)))
                .thenAnswer(invocation -> Optional.ofNullable(savedRefreshToken.get()));

        MvcResult refreshResult = mockMvc.perform(post("/public/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        JsonNode refreshResponse = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String newAccessToken = refreshResponse.get("accessToken").asText();

        JwtService.AccessTokenClaims refreshedClaims = jwtService.parseAccessToken(newAccessToken);
        assertThat(refreshedClaims.username()).isEqualTo("admin");
        assertThat(refreshedClaims.sessionId()).isNotBlank();
        assertThat(refreshedClaims.sessionId()).isNotEqualTo(claims.sessionId());
    }

    @Test
    void shouldChangePasswordWithRealAuthServiceAndInvalidateCurrentSession() throws Exception {
        Usuario usuario = usuarioComPermissao("admin", "senhaAtual123", "USUARIO", "VISUALIZAR");
        AtomicReference<Usuario> persistedUser = new AtomicReference<>(usuario);
        List<RefreshToken> refreshTokens = new ArrayList<>();

        when(usuarioRepository.findByUsername("admin")).thenAnswer(invocation -> Optional.of(persistedUser.get()));
        when(usuarioRepository.findWithAuthorizationByUsername("admin")).thenAnswer(invocation -> Optional.of(persistedUser.get()));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario savedUsuario = invocation.getArgument(0);
            persistedUser.set(savedUsuario);
            return savedUsuario;
        });
        when(refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenAnswer(invocation -> refreshTokens.stream()
                        .filter(token -> token.getUsuario() == usuario)
                        .filter(token -> !token.isRevoked() && !token.isExpired())
                        .count());
        when(refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(eq(usuario), any(LocalDateTime.class)))
                .thenAnswer(invocation -> refreshTokens.stream()
                        .filter(token -> token.getUsuario() == usuario)
                        .filter(token -> !token.isRevoked() && !token.isExpired())
                        .toList());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken refreshToken = invocation.getArgument(0);
            if (!refreshTokens.contains(refreshToken)) {
                refreshTokens.add(refreshToken);
            }
            return refreshToken;
        });
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    String sessionId = invocation.getArgument(0);
                    LocalDateTime referenceTime = invocation.getArgument(1);
                    return refreshTokens.stream()
                            .anyMatch(token -> token.getSessionId().equals(sessionId)
                                    && !token.isRevoked()
                                    && token.getExpiresAt().isAfter(referenceTime));
                });
        when(blacklistedTokenRepository.existsByTokenHash(anyString())).thenReturn(false);
        when(usuarioService.get()).thenReturn(List.of(new UsuarioDto(1L, "admin", "Administrador", null)));

        MvcResult loginResult = mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"senhaAtual123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode authResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = authResponse.get("accessToken").asText();

        mockMvc.perform(put("/api/auth/password")
                        .servletPath("/api/auth/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"senhaAtual123\",\"newPassword\":\"novaSenha123\"}"))
                .andExpect(status().isNoContent());

        assertThat(passwordService.matches("novaSenha123", persistedUser.get().getPassword())).isTrue();
        assertThat(refreshTokens).isNotEmpty();
        assertThat(refreshTokens).allMatch(RefreshToken::isRevoked);

        mockMvc.perform(get("/api/auth/me")
                        .servletPath("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_SESSION_INVALID"));

        mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"senhaAtual123\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"novaSenha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    private Usuario usuarioComPermissao(String username, String rawPassword, String papelCodigo, String permissaoCodigo) {
        Perfil perfil = new Perfil("PADRAO", "Perfil padrao");
        ReflectionTestUtils.setField(perfil, "id", 10L);
        perfil.adicionarAutorizacao(
                new Papel(papelCodigo, "Papel " + papelCodigo),
                new Permissao(permissaoCodigo, "Permissao " + permissaoCodigo)
        );
        Usuario usuario = new Usuario(username, "Administrador", passwordService.encode(rawPassword), perfil);
        ReflectionTestUtils.setField(usuario, "id", 1L);
        return usuario;
    }
}
