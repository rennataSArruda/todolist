package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.TarefaCategoriaDto;
import br.com.rennataarruda.todolist.entity.TarefaCategoria;
import br.com.rennataarruda.todolist.mapper.TarefaCategoriaMapper;
import br.com.rennataarruda.todolist.repository.TarefaCategoriaRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaCategoriaServiceTest {

    @Mock
    private TarefaCategoriaRepository repository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private final TarefaCategoriaMapper mapper = new TarefaCategoriaMapper();

    @Test
    void shouldRejectCreateWhenNomeIsMissing() {
        TarefaCategoriaService service = newService();

        assertThatThrownBy(() -> service.create(new TarefaCategoriaDto(null, null, null, "Descricao", null, null, true, null, null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Nome e obrigatorio");
    }

    @Test
    void shouldRejectCreateWhenCorHexIsInvalid() {
        TarefaCategoriaService service = newService();

        assertThatThrownBy(() -> service.create(new TarefaCategoriaDto(
                null, null, "Trabalho", "Descricao", "#12ABGZ", "check_circle", true, null, null
        )))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cor HEX invalida");
    }

    @Test
    void shouldSetAuthenticatedUserIdOnCreateIgnoringDtoUsuarioId() {
        TarefaCategoriaService service = newService();
        when(authenticatedUserProvider.currentUserId()).thenReturn(10L);
        when(repository.save(any(TarefaCategoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaCategoriaDto dto = service.create(new TarefaCategoriaDto(
                null,
                999L,
                "Trabalho",
                "Categorias de trabalho",
                "#AABBCC",
                "check_circle",
                true,
                null,
                null
        ));

        assertThat(dto.usuarioId()).isEqualTo(10L);
        assertThat(dto.nome()).isEqualTo("Trabalho");
        assertThat(dto.descricao()).isEqualTo("Categorias de trabalho");
        assertThat(dto.corHex()).isEqualTo("#AABBCC");
        assertThat(dto.icone()).isEqualTo("check_circle");
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    void shouldKeepAuthenticatedUserIdAndAtivoOnUpdateIgnoringDtoValues() {
        TarefaCategoriaService service = newService();
        TarefaCategoria entity = new TarefaCategoria("Casa", "Antiga", "#FFFFFF", "home", true);
        entity.definirUsuarioId(10L);

        when(authenticatedUserProvider.currentUserId()).thenReturn(10L);
        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<TarefaCategoria>>any()))
                .thenReturn(Optional.of(entity));
        when(repository.save(any(TarefaCategoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaCategoriaDto dto = service.update(1L, new TarefaCategoriaDto(
                1L,
                999L,
                "Casa",
                "Atualizada",
                "#00AA11",
                "star",
                false,
                null,
                null
        ));

        assertThat(dto.usuarioId()).isEqualTo(10L);
        assertThat(dto.descricao()).isEqualTo("Atualizada");
        assertThat(dto.corHex()).isEqualTo("#00AA11");
        assertThat(dto.icone()).isEqualTo("star");
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    void shouldToggleAtivoWhenBloquear() {
        TarefaCategoriaService service = newService();
        TarefaCategoria entity = new TarefaCategoria("Casa", "Antiga", "#FFFFFF", "home", true);
        entity.definirUsuarioId(10L);

        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<TarefaCategoria>>any()))
                .thenReturn(Optional.of(entity));
        when(repository.save(any(TarefaCategoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaCategoriaDto dto = service.bloquear(1L);

        assertThat(dto.ativo()).isFalse();
        verify(repository).save(entity);
    }

    private TarefaCategoriaService newService() {
        return new TarefaCategoriaService(repository, authenticatedUserProvider, mapper);
    }
}
