package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.PerfilDto;
import br.com.rennataarruda.todolist.dto.PerfilPermissaoDto;
import br.com.rennataarruda.todolist.mapper.PerfilMapper;
import br.com.rennataarruda.todolist.repository.PapelPermissaoRepository;
import br.com.rennataarruda.todolist.repository.PapelRepository;
import br.com.rennataarruda.todolist.repository.PerfilPapelPermissaoRepository;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.repository.PermissaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private PerfilRepository repository;

    @Mock
    private PapelRepository papelRepository;

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private PerfilPapelPermissaoRepository perfilPapelPermissaoRepository;

    @Mock
    private PapelPermissaoRepository papelPermissaoRepository;

    private final PerfilMapper mapper = new PerfilMapper();

    @Test
    void shouldRejectCreateWhenCodigoIsMissing() {
        PerfilService service = service();

        assertThatThrownBy(() -> service.create(new PerfilDto(null, null, "Descricao")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Codigo e obrigatorio");
    }

    @Test
    void shouldRejectCreateWhenCodigoAlreadyExists() {
        PerfilService service = service();
        when(repository.existsByCodigo("ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new PerfilDto(null, "ADMIN", "Administrador")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Codigo de perfil ja cadastrado");
    }

    @Test
    void shouldRejectUpdateWhenCodigoAlreadyExistsForAnotherRecord() {
        PerfilService service = service();
        when(repository.existsByCodigoAndIdNot("ADMIN", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(10L, new PerfilDto(10L, "ADMIN", "Administrador")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Codigo de perfil ja cadastrado");
    }

    @Test
    void shouldRejectWhenPermissionIsNotAllowedForRole() {
        PerfilService service = service();
        String papel = "USUARIO";
        String permissao = "BLOQUEAR";
        
        PerfilPermissaoDto permissaoDto = new PerfilPermissaoDto(papel, permissao);
        PerfilDto dto = new PerfilDto(null, "OPERADOR", "Perfil Operador", List.of(permissaoDto));

        when(repository.existsByCodigo("OPERADOR")).thenReturn(false);
        when(papelPermissaoRepository.existsByPapelCodigoAndPermissaoCodigo(papel, permissao)).thenReturn(false);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("O papel USUARIO nao possui permissao para BLOQUEAR");
    }

    private PerfilService service() {
        return new PerfilService(
                repository,
                papelRepository,
                permissaoRepository,
                perfilPapelPermissaoRepository,
                papelPermissaoRepository,
                mapper
        );
    }
}
