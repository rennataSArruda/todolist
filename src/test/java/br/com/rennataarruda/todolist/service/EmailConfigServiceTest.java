package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.config.ApplicationCryptoProperties;
import br.com.rennataarruda.todolist.dto.EmailConfigDto;
import br.com.rennataarruda.todolist.entity.EmailConfig;
import br.com.rennataarruda.todolist.mapper.EmailConfigMapper;
import br.com.rennataarruda.todolist.repository.EmailConfigRepository;
import br.com.rennataarruda.todolist.service.email.EmailConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailConfigServiceTest {

    @Mock
    private EmailConfigRepository repository;

    private final EmailConfigMapper mapper = new EmailConfigMapper();
    private final ApplicationCryptoService cryptoService = newCryptoService();

    @Test
    void shouldCreateActiveConfigAndDeactivatePreviousActiveConfigs() {
        EmailConfigService service = newService();
        EmailConfig previousActive = new EmailConfig(
                "smtp.old.com",
                587,
                "old-user",
                cryptoService.encryptIfNeeded("old-password"),
                "old@email.com",
                "Old",
                true,
                true,
                false,
                true
        );

        when(repository.findByAtivoTrue()).thenReturn(List.of(previousActive));
        when(repository.save(any(EmailConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmailConfigDto dto = service.create(validDto(true));

        assertThat(previousActive.getAtivo()).isFalse();
        assertThat(dto.ativo()).isTrue();
        assertThat(dto.password()).isNull();
        verify(repository).saveAll(List.of(previousActive));
    }

    @Test
    void shouldEncryptPasswordWhenCreatingConfig() {
        EmailConfigService service = newService();

        when(repository.save(any(EmailConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(validDto(false));

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(entity ->
                !"senha".equals(entity.getPassword())
                        && cryptoService.isEncrypted(entity.getPassword())
                        && "senha".equals(cryptoService.decryptIfNeeded(entity.getPassword()))
        ));
    }

    @Test
    void shouldReturnDecryptedPasswordWhenGettingConfigDetail() {
        EmailConfigService service = newService();
        EmailConfig entity = new EmailConfig(
                "smtp.email.com",
                587,
                "usuario",
                cryptoService.encryptIfNeeded("senha"),
                "noreply@email.com",
                "Todo List",
                true,
                true,
                false,
                true
        );

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        EmailConfigDto dto = service.getByIdWithPassword(1L);

        assertThat(dto.password()).isEqualTo("senha");
    }

    @Test
    void shouldRejectInvalidPort() {
        EmailConfigService service = newService();
        EmailConfigDto dto = new EmailConfigDto(
                null,
                "smtp.email.com",
                70000,
                "usuario",
                "senha",
                "noreply@email.com",
                "Todo List",
                true,
                true,
                false,
                false,
                null,
                null
        );

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Porta inválida");
    }

    @Test
    void shouldKeepCurrentPasswordWhenUpdateDoesNotSendPassword() {
        EmailConfigService service = newService();
        String encryptedPassword = cryptoService.encryptIfNeeded("senha-antiga");
        EmailConfig entity = new EmailConfig(
                "smtp.email.com",
                587,
                "usuario",
                encryptedPassword,
                "noreply@email.com",
                "Todo List",
                true,
                true,
                false,
                false
        );

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(EmailConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmailConfigDto dto = service.update(1L, new EmailConfigDto(
                1L,
                "smtp.novo.com",
                465,
                "novo-usuario",
                null,
                "no-reply@novo.com",
                "Novo Nome",
                true,
                false,
                true,
                false,
                null,
                null
        ));

        assertThat(entity.getPassword()).isEqualTo(encryptedPassword);
        assertThat(cryptoService.decryptIfNeeded(entity.getPassword())).isEqualTo("senha-antiga");
        assertThat(dto.host()).isEqualTo("smtp.novo.com");
        assertThat(dto.password()).isNull();
    }

    @Test
    void shouldFindActiveConfig() {
        EmailConfigService service = newService();
        EmailConfig active = new EmailConfig(
                "smtp.email.com",
                587,
                "usuario",
                cryptoService.encryptIfNeeded("senha"),
                "noreply@email.com",
                "Todo List",
                true,
                true,
                false,
                true
        );

        when(repository.findFirstByAtivoTrueOrderByIdDesc()).thenReturn(Optional.of(active));

        Optional<EmailConfig> result = service.findActiveConfig();

        assertThat(result).contains(active);
    }

    private EmailConfigDto validDto(Boolean ativo) {
        return new EmailConfigDto(
                null,
                "smtp.email.com",
                587,
                "usuario",
                "senha",
                "noreply@email.com",
                "Todo List",
                true,
                true,
                false,
                ativo,
                null,
                null
        );
    }

    private EmailConfigService newService() {
        return new EmailConfigService(repository, mapper, cryptoService);
    }

    private ApplicationCryptoService newCryptoService() {
        ApplicationCryptoProperties properties = new ApplicationCryptoProperties();
        properties.setSecret("test-secret-for-email-config-crypto");
        return new ApplicationCryptoService(properties);
    }
}
