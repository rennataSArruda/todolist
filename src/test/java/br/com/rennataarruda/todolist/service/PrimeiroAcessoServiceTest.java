package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.FirstAccessRequest;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrimeiroAcessoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConfiguracoesGeraisService configuracoesGeraisService;

    @Mock
    private PasswordService passwordService;

    @Test
    void shouldCreateNonRootUserWithConfiguredDefaultProfile() {
        PrimeiroAcessoService service = newService();
        Perfil perfil = new Perfil("COLABORADOR", "Perfil de colaborador");

        when(usuarioRepository.existsByUsername("maria")).thenReturn(false);
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(configuracoesGeraisService.getPerfilPadraoPrimeiroAcesso()).thenReturn(perfil);
        when(passwordService.encode("senha123")).thenReturn("encoded-password");

        service.criar(new FirstAccessRequest("maria", "maria@email.com", "Maria", "senha123"));

        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(usuario ->
                !usuario.isRoot()
                        && usuario.getPerfil() == perfil
                        && "encoded-password".equals(usuario.getPassword())
        ));
    }

    @Test
    void shouldRejectDuplicateUsername() {
        PrimeiroAcessoService service = newService();

        when(usuarioRepository.existsByUsername("maria")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(validRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username ja cadastrado");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        PrimeiroAcessoService service = newService();

        when(usuarioRepository.existsByUsername("maria")).thenReturn(false);
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(validRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email ja cadastrado");
    }

    @Test
    void shouldRejectShortPassword() {
        PrimeiroAcessoService service = newService();

        assertThatThrownBy(() -> service.criar(new FirstAccessRequest(
                "maria",
                "maria@email.com",
                "Maria",
                "1234567"
        )))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Senha deve ter pelo menos 8 caracteres");
    }

    private FirstAccessRequest validRequest() {
        return new FirstAccessRequest("maria", "maria@email.com", "Maria", "senha123");
    }

    private PrimeiroAcessoService newService() {
        return new PrimeiroAcessoService(
                usuarioRepository,
                configuracoesGeraisService,
                passwordService
        );
    }
}
