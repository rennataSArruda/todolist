package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityRequest;
import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityResponse;
import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityType;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrimeiroAcessoDisponibilidadeServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void shouldReturnUnavailableWhenUsernameExists() {
        PrimeiroAcessoDisponibilidadeService service = new PrimeiroAcessoDisponibilidadeService(usuarioRepository);
        when(usuarioRepository.existsByUsername("maria")).thenReturn(true);

        FirstAccessAvailabilityResponse response = service.verificar(
                new FirstAccessAvailabilityRequest(FirstAccessAvailabilityType.USERNAME, "maria")
        );

        assertThat(response.available()).isFalse();
    }

    @Test
    void shouldReturnAvailableWhenEmailDoesNotExist() {
        PrimeiroAcessoDisponibilidadeService service = new PrimeiroAcessoDisponibilidadeService(usuarioRepository);
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(false);

        FirstAccessAvailabilityResponse response = service.verificar(
                new FirstAccessAvailabilityRequest(FirstAccessAvailabilityType.EMAIL, "maria@email.com")
        );

        assertThat(response.available()).isTrue();
    }

    @Test
    void shouldRejectMissingValue() {
        PrimeiroAcessoDisponibilidadeService service = new PrimeiroAcessoDisponibilidadeService(usuarioRepository);

        assertThatThrownBy(() -> service.verificar(
                new FirstAccessAvailabilityRequest(FirstAccessAvailabilityType.EMAIL, " ")
        ))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Valor para verificacao de disponibilidade e obrigatorio");
    }
}
