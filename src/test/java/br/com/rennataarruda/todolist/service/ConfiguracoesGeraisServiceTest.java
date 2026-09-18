package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.ConfiguracoesGeraisDto;
import br.com.rennataarruda.todolist.entity.ConfiguracoesGerais;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.mapper.ConfiguracoesGeraisMapper;
import br.com.rennataarruda.todolist.repository.ConfiguracoesGeraisRepository;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfiguracoesGeraisServiceTest {

    @Mock
    private ConfiguracoesGeraisRepository configuracoesGeraisRepository;

    @Mock
    private PerfilRepository perfilRepository;

    private final ConfiguracoesGeraisMapper mapper = new ConfiguracoesGeraisMapper();

    @Test
    void shouldReturnCurrentConfiguration() {
        ConfiguracoesGeraisService service = newService();
        ConfiguracoesGerais configuracoes = new ConfiguracoesGerais(null);

        when(configuracoesGeraisRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(configuracoes));

        ConfiguracoesGeraisDto dto = service.get();

        assertThat(dto.perfilPadraoPrimeiroAcessoId()).isNull();
    }

    @Test
    void shouldUpdateDefaultFirstAccessProfile() {
        ConfiguracoesGeraisService service = newService();
        ConfiguracoesGerais configuracoes = new ConfiguracoesGerais(null);
        Perfil perfil = new Perfil("COLABORADOR", "Perfil de colaborador");

        when(configuracoesGeraisRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(configuracoes));
        when(perfilRepository.findById(10L)).thenReturn(Optional.of(perfil));
        when(configuracoesGeraisRepository.save(any(ConfiguracoesGerais.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.atualizarPerfilPadraoPrimeiroAcesso(10L);

        assertThat(configuracoes.getPerfilPadraoPrimeiroAcesso()).isSameAs(perfil);
        verify(configuracoesGeraisRepository).save(configuracoes);
    }

    @Test
    void shouldRejectMissingDefaultProfileId() {
        ConfiguracoesGeraisService service = newService();

        assertThatThrownBy(() -> service.atualizarPerfilPadraoPrimeiroAcesso(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Perfil padrao de primeiro acesso e obrigatorio");
    }

    @Test
    void shouldRejectUnknownDefaultProfile() {
        ConfiguracoesGeraisService service = newService();

        when(perfilRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarPerfilPadraoPrimeiroAcesso(10L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Perfil padrao de primeiro acesso nao encontrado");
    }

    @Test
    void shouldRejectFirstAccessWhenDefaultProfileIsNotConfigured() {
        ConfiguracoesGeraisService service = newService();

        when(configuracoesGeraisRepository.findFirstByOrderByIdAsc())
                .thenReturn(Optional.of(new ConfiguracoesGerais(null)));

        assertThatThrownBy(service::getPerfilPadraoPrimeiroAcesso)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Perfil padrao de primeiro acesso nao configurado");
    }

    @Test
    void shouldFailWhenConfigurationWasNotInitialized() {
        ConfiguracoesGeraisService service = newService();

        when(configuracoesGeraisRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        assertThatThrownBy(service::get)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Configuracoes gerais nao inicializadas");
    }

    private ConfiguracoesGeraisService newService() {
        return new ConfiguracoesGeraisService(
                configuracoesGeraisRepository,
                perfilRepository,
                mapper
        );
    }
}
