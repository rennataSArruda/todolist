package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.entity.EmailConfig;
import br.com.rennataarruda.todolist.service.email.EmailConfigService;
import br.com.rennataarruda.todolist.service.email.EmailTemplateService;
import br.com.rennataarruda.todolist.service.email.MailService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private EmailConfigService emailConfigService;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void shouldReturnFalseWhenThereIsNoActiveEmailConfig() {
        TestableMailService service = new TestableMailService(emailConfigService, emailTemplateService, mailSender);
        when(emailConfigService.findActiveConfig()).thenReturn(Optional.empty());

        boolean sent = service.sendPasswordReset("user@email.com", "https://app/reset?token=abc");

        assertThat(sent).isFalse();
        verify(mailSender, never()).send(org.mockito.ArgumentMatchers.any(MimeMessage.class));
    }

    @Test
    void shouldSendPasswordResetWithActiveEmailConfig() throws Exception {
        TestableMailService service = new TestableMailService(emailConfigService, emailTemplateService, mailSender);
        EmailConfig config = activeConfig();
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        String resetLink = "https://app/reset?token=abc";

        when(emailConfigService.findActiveConfig()).thenReturn(Optional.of(config));
        when(emailTemplateService.passwordReset(resetLink)).thenReturn("<p>Redefinir senha</p>");
        when(mailSender.createMimeMessage()).thenReturn(message);

        boolean sent = service.sendPasswordReset("user@email.com", resetLink);

        assertThat(sent).isTrue();
        assertThat(service.config).isSameAs(config);
        assertThat(message.getSubject()).isEqualTo("Redefinição de senha");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("user@email.com");
        assertThat(message.getFrom()[0].toString()).contains("no-reply@email.com");
        verify(emailTemplateService).passwordReset(resetLink);
        verify(mailSender).send(message);
    }

    @Test
    void shouldReturnFalseWhenMailSenderFails() throws Exception {
        TestableMailService service = new TestableMailService(emailConfigService, emailTemplateService, mailSender);
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        String resetLink = "https://app/reset?token=abc";

        when(emailConfigService.findActiveConfig()).thenReturn(Optional.of(activeConfig()));
        when(emailTemplateService.passwordReset(resetLink)).thenReturn("<p>Redefinir senha</p>");
        when(mailSender.createMimeMessage()).thenReturn(message);
        doThrow(new MailSendException("falha smtp")).when(mailSender).send(message);

        boolean sent = service.sendPasswordReset("user@email.com", resetLink);

        assertThat(sent).isFalse();
    }

    @Test
    void shouldCreateMailSenderFromConfig() {
        TestableMailService service = new TestableMailService(emailConfigService, emailTemplateService, mailSender);
        when(emailConfigService.decryptPassword("smtp-password")).thenReturn("smtp-password");

        JavaMailSenderImpl sender = (JavaMailSenderImpl) service.createMailSenderForTest(activeConfig());

        assertThat(sender.getHost()).isEqualTo("smtp.email.com");
        assertThat(sender.getPort()).isEqualTo(587);
        assertThat(sender.getUsername()).isEqualTo("smtp-user");
        assertThat(sender.getPassword()).isEqualTo("smtp-password");
        assertThat(sender.getJavaMailProperties().get("mail.smtp.auth")).isEqualTo(true);
        assertThat(sender.getJavaMailProperties().get("mail.smtp.starttls.enable")).isEqualTo(true);
        assertThat(sender.getJavaMailProperties().get("mail.smtp.ssl.enable")).isEqualTo(false);
    }

    private EmailConfig activeConfig() {
        return new EmailConfig(
                "smtp.email.com",
                587,
                "smtp-user",
                "smtp-password",
                "no-reply@email.com",
                "Todo List",
                true,
                true,
                false,
                true
        );
    }

    private static class TestableMailService extends MailService {

        private final JavaMailSender mailSender;
        private EmailConfig config;

        private TestableMailService(
                EmailConfigService emailConfigService,
                EmailTemplateService emailTemplateService,
                JavaMailSender mailSender
        ) {
            super(emailConfigService, emailTemplateService);
            this.mailSender = mailSender;
        }

        JavaMailSender createMailSenderForTest(EmailConfig config) {
            return super.createMailSender(config);
        }

        @Override
        protected JavaMailSender createMailSender(EmailConfig config) {
            this.config = config;
            return mailSender;
        }
    }
}