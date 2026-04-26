package br.com.rennataarruda.todolist.security;

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
import br.com.rennataarruda.todolist.repository.view.TarefaAnaliticoViewRepository;
import br.com.rennataarruda.todolist.repository.view.TarefaViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.config.import=",
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
        "app.security.jwt.secret=12345678901234567890123456789012"
})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SwaggerSecurityDevIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private BlacklistedTokenRepository blacklistedTokenRepository;

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
    private TarefaAnaliticoViewRepository tarefaAnaliticoViewRepository;

    @MockBean
    private TarefaStatusRepository tarefaStatusRepository;

    @MockBean
    private TarefaPrioridadeRepository tarefaPrioridadeRepository;

    @Test
    void shouldAllowSwaggerInDevProfile() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
