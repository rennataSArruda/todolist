package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.PerfilDto;
import br.com.rennataarruda.todolist.mapper.PerfilMapper;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private PerfilRepository repository;

    private final PerfilMapper mapper = new PerfilMapper();

    @Test
    void shouldRejectCreateWhenCodigoIsMissing() {
        PerfilService service = new PerfilService(repository, mapper);

        assertThatThrownBy(() -> service.create(new PerfilDto(null, null, "Descricao")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Codigo e obrigatorio");
    }

    @Test
    void shouldRejectCreateWhenCodigoAlreadyExists() {
        PerfilService service = new PerfilService(repository, mapper);
        when(repository.existsByCodigo("ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new PerfilDto(null, "ADMIN", "Administrador")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Codigo de perfil ja cadastrado");
    }

    @Test
    void shouldRejectUpdateWhenCodigoAlreadyExistsForAnotherRecord() {
        PerfilService service = new PerfilService(repository, mapper);
        when(repository.existsByCodigoAndIdNot("ADMIN", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(10L, new PerfilDto(10L, "ADMIN", "Administrador")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Codigo de perfil ja cadastrado");
    }
}
