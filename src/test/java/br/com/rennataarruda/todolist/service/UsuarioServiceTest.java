package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.mapper.UsuarioMapper;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordService passwordService;

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void shouldKeepAtivoOnUpdateIgnoringDtoAtivo() {
        UsuarioService service = newService();
        Usuario usuario = new Usuario("admin", "Administrador", "encoded", new Perfil("PADRAO", "Perfil padrao"));

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.existsByUsernameAndIdNot("admin", 1L)).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDto dto = service.update(1L, new UsuarioDto(1L, "admin", "Novo nome", false, null));

        assertThat(dto.name()).isEqualTo("Novo nome");
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    void shouldToggleAtivoWhenBloquear() {
        UsuarioService service = newService();
        Usuario usuario = new Usuario("admin", "Administrador", "encoded", new Perfil("PADRAO", "Perfil padrao"));

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDto dto = service.bloquear(1L);

        assertThat(dto.ativo()).isFalse();
        verify(repository).save(usuario);
    }

    private UsuarioService newService() {
        return new UsuarioService(repository, perfilRepository, mapper, passwordService);
    }
}
