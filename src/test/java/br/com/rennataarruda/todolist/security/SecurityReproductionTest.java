package br.com.rennataarruda.todolist.security;

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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
        "app.security.jwt.secret=12345678901234567890123456789012"
})
@AutoConfigureMockMvc
class SecurityReproductionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserAuthorityService userAuthorityService;

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
    private PapelPermissaoRepository papelPermissaoRepository;

    @Test
    void shouldDenySearchWhenUserHasZeroRelevantPermissions() throws Exception {
        // GIVEN: User with only USUARIO_VISUALIZAR permission
        Usuario usuario = usuarioComApenasVisualizarUsuario("user_wrong_perm", "senha123");

        when(usuarioRepository.findByUsername("user_wrong_perm")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findWithAuthorizationByUsername("user_wrong_perm")).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(blacklistedTokenRepository.existsByTokenHash(anyString())).thenReturn(false);

        // LOGIN
        MvcResult loginResult = mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user_wrong_perm\",\"password\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode authResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = authResponse.get("accessToken").asText();

        // WHEN: Access /api/tarefa/search (which requires TAREFA_VISUALIZAR)
        // THEN: Should be Forbidden (403)
        mockMvc.perform(post("/api/tarefa/search")
                        .servletPath("/api/tarefa/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDenySearchWhenUserOnlyHasCreatePermission() throws Exception {
        // GIVEN: User with only TAREFA_CRIAR permission
        Usuario usuario = usuarioComApenasCriarTarefa("user_only_create", "senha123");

        when(usuarioRepository.findByUsername("user_only_create")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findWithAuthorizationByUsername("user_only_create")).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(blacklistedTokenRepository.existsByTokenHash(anyString())).thenReturn(false);

        // LOGIN
        MvcResult loginResult = mockMvc.perform(post("/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user_only_create\",\"password\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode authResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = authResponse.get("accessToken").asText();

        // WHEN: Access /api/tarefa/search (which requires TAREFA_VISUALIZAR)
        // THEN: Should be Forbidden (403)
        mockMvc.perform(post("/api/tarefa/search")
                        .servletPath("/api/tarefa/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    private Usuario usuarioComApenasVisualizarUsuario(String username, String rawPassword) {
        Perfil perfil = new Perfil("TESTE", "Perfil teste");
        ReflectionTestUtils.setField(perfil, "id", 99L);
        perfil.adicionarAutorizacao(
                new Papel("USUARIO", "Recurso de usuarios"),
                new Permissao("VISUALIZAR", "Permissao de visualizacao")
        );
        Usuario usuario = new Usuario(username, "Teste", passwordService.encode(rawPassword), perfil);
        ReflectionTestUtils.setField(usuario, "id", 1L);
        return usuario;
    }

    private Usuario usuarioComApenasCriarTarefa(String username, String rawPassword) {
        Perfil perfil = new Perfil("OPERADOR", "Perfil operador");
        ReflectionTestUtils.setField(perfil, "id", 23L);
        perfil.adicionarAutorizacao(
                new Papel("TAREFA", "Recurso de tarefas"),
                new Permissao("CRIAR", "Permissao de criacao")
        );
        Usuario usuario = new Usuario(username, "Operador", passwordService.encode(rawPassword), perfil);
        ReflectionTestUtils.setField(usuario, "id", 1L);
        return usuario;
    }
}
