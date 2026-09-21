package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.RefreshToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.mapper.UsuarioMapper;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void shouldCreateUserWithEmail() {
        UsuarioService service = newService();
        Perfil perfil = new Perfil("PADRAO", "Perfil padrao");

        when(repository.existsByUsername("admin")).thenReturn(false);
        when(repository.existsByEmail("admin@email.com")).thenReturn(false);
        when(passwordService.encode("senha123")).thenReturn("encoded");
        when(perfilRepository.findByCodigo("PADRAO")).thenReturn(Optional.of(perfil));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDto dto = service.create(new UsuarioDto(null, "admin", "admin@email.com", "Administrador", null, "senha123"));

        assertThat(dto.username()).isEqualTo("admin");
        assertThat(dto.email()).isEqualTo("admin@email.com");
        assertThat(dto.name()).isEqualTo("Administrador");
    }

    @Test
    void shouldRejectCreateWithDuplicatedEmail() {
        UsuarioService service = newService();

        when(repository.existsByUsername("admin")).thenReturn(false);
        when(repository.existsByEmail("admin@email.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new UsuarioDto(null, "admin", "admin@email.com", "Administrador", null, "senha123")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email ja cadastrado");
    }

    @Test
    void shouldRejectUpdateWithDuplicatedEmailFromAnotherUser() {
        UsuarioService service = newService();

        when(repository.existsByUsernameAndIdNot("admin", 1L)).thenReturn(false);
        when(repository.existsByEmailAndIdNot("admin@email.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L, new UsuarioDto(1L, "admin", "admin@email.com", "Administrador", true, null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email ja cadastrado");
    }

    @Test
    void shouldKeepAtivoOnUpdateIgnoringDtoAtivo() {
        UsuarioService service = newService();
        Usuario usuario = new Usuario("admin", "admin@email.com", "Administrador", "encoded", new Perfil("PADRAO", "Perfil padrao"));

        when(repository.existsByUsernameAndIdNot("admin", 1L)).thenReturn(false);
        when(repository.existsByEmailAndIdNot("admin@email.com", 1L)).thenReturn(false);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDto dto = service.update(1L, new UsuarioDto(1L, "admin", "admin@email.com", "Novo nome", false, null));

        assertThat(dto.name()).isEqualTo("Novo nome");
        assertThat(dto.email()).isEqualTo("admin@email.com");
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    void shouldToggleAtivoWhenBloquear() {
        UsuarioService service = newService();
        Usuario usuario = new Usuario("admin", "admin@email.com", "Administrador", "encoded", new Perfil("PADRAO", "Perfil padrao"));

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDto dto = service.bloquear(1L);

        assertThat(dto.ativo()).isFalse();
        verify(repository).save(usuario);
    }

    @Test
    void shouldRevokeActiveSessionsWhenBloquearTurnsUserInactive() {
        UsuarioService service = newService();
        Usuario usuario = new Usuario("admin", "admin@email.com", "Administrador", "encoded", new Perfil("PADRAO", "Perfil padrao"));
        RefreshToken refreshToken = new RefreshToken(
                "token-hash",
                "session-1",
                usuario,
                LocalDateTime.now().plusDays(1)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(List.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDto dto = service.bloquear(1L);

        assertThat(dto.ativo()).isFalse();
        assertThat(refreshToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(refreshToken);
    }

    private UsuarioService newService() {
        return new UsuarioService(repository, perfilRepository, mapper, passwordService, refreshTokenRepository);
    }
}
