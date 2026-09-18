package br.com.rennataarruda.todolist.service.email;

import br.com.rennataarruda.todolist.entity.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String SMTP_AUTH_PROPERTY = "mail.smtp.auth";
    private static final String SMTP_STARTTLS_PROPERTY = "mail.smtp.starttls.enable";
    private static final String SMTP_SSL_PROPERTY = "mail.smtp.ssl.enable";

    private final EmailConfigService emailConfigService;
    private final EmailTemplateService emailTemplateService;

    public MailService(
            EmailConfigService emailConfigService,
            EmailTemplateService emailTemplateService
    ) {
        this.emailConfigService = emailConfigService;
        this.emailTemplateService = emailTemplateService;
    }

    public boolean sendPasswordReset(String to, String resetLink) {
        return emailConfigService.findActiveConfig()
                .map(config -> sendPasswordReset(config, to, resetLink))
                .orElseGet(() -> {
                    LOGGER.warn("Password reset email was not sent because there is no active email configuration");
                    return false;
                });
    }

    private boolean sendPasswordReset(EmailConfig config, String to, String resetLink) {
        try {
            JavaMailSender mailSender = createMailSender(config);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

            helper.setFrom(fromAddress(config));
            helper.setTo(to);
            helper.setSubject("Redefinição de senha");

            helper.setText(emailTemplateService.passwordReset(resetLink), true);

            mailSender.send(message);
            return true;
        } catch (MailException | MessagingException | UnsupportedEncodingException exception) {
            LOGGER.error("Failed to send password reset email to {}", to, exception);
            return false;
        }
    }

    protected JavaMailSender createMailSender(EmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(emailConfigService.decryptPassword(config.getPassword()));
        mailSender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        mailSender.setJavaMailProperties(mailProperties(config));
        return mailSender;
    }

    private Properties mailProperties(EmailConfig config) {
        Properties properties = new Properties();
        properties.put(SMTP_AUTH_PROPERTY, Boolean.TRUE.equals(config.getAuth()));
        properties.put(SMTP_STARTTLS_PROPERTY, Boolean.TRUE.equals(config.getStartTls()));
        properties.put(SMTP_SSL_PROPERTY, Boolean.TRUE.equals(config.getSsl()));
        return properties;
    }

    private InternetAddress fromAddress(EmailConfig config) throws UnsupportedEncodingException, AddressException {
        if (StringUtils.hasText(config.getFromName())) {
            return new InternetAddress(config.getFromAddress(), config.getFromName(), StandardCharsets.UTF_8.name());
        }
        return new InternetAddress(config.getFromAddress());
    }
}
