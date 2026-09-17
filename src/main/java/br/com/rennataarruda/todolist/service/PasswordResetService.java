package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.ForgotPasswordRequest;
import br.com.rennataarruda.todolist.dto.auth.ResetPasswordRequest;
import br.com.rennataarruda.todolist.entity.PasswordResetToken;
import br.com.rennataarruda.todolist.entity.RefreshToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.PasswordResetTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.PasswordService;
import br.com.rennataarruda.todolist.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordService passwordService;
    private final MailService mailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String resetUrlTemplate;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordService passwordService,
            MailService mailService,
            @Value("${app.password-reset.reset-url-template:http://localhost:4200/reset-password?token={token}}") String resetUrlTemplate
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordService = passwordService;
        this.mailService = mailService;
        this.resetUrlTemplate = resetUrlTemplate;
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        if (request == null || !StringUtils.hasText(request.identifier())) {
            return;
        }

        Optional<Usuario> usuario = findByIdentifier(request.identifier().trim());
        if (usuario.isEmpty() || !Boolean.TRUE.equals(usuario.get().getAtivo())) {
            return;
        }

        createAndSendResetToken(usuario.get());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        validateResetRequest(request);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByTokenHash(hashToken(request.token()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de recuperacao invalido"));

        if (passwordResetToken.isUsed() || passwordResetToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de recuperacao invalido");
        }

        Usuario usuario = passwordResetToken.getUsuario();
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de recuperacao invalido");
        }

        usuario.alterarSenha(passwordService.encode(request.newPassword()));
        passwordResetToken.markAsUsed();
        passwordResetTokenRepository.save(passwordResetToken);
        usuarioRepository.save(usuario);
        revokeAllActiveSessions(usuario);
    }

    private void createAndSendResetToken(Usuario usuario) {
        invalidateActiveResetTokens(usuario);

        String token = generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                hashToken(token),
                usuario,
                LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES)
        );
        passwordResetTokenRepository.save(passwordResetToken);

        boolean sent = mailService.sendPasswordReset(usuario.getEmail(), buildResetLink(token));
        if (!sent) {
            passwordResetToken.markAsUsed();
            passwordResetTokenRepository.save(passwordResetToken);
        }
    }

    private Optional<Usuario> findByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return usuarioRepository.findByEmail(identifier);
        }
        return usuarioRepository.findByUsername(identifier);
    }

    private void invalidateActiveResetTokens(Usuario usuario) {
        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(usuario, now)
                .forEach(token -> {
                    token.markAsUsed();
                    passwordResetTokenRepository.save(token);
                });
    }

    private void revokeAllActiveSessions(Usuario usuario) {
        LocalDateTime now = LocalDateTime.now();
        for (RefreshToken activeSession : refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(usuario, now)) {
            activeSession.revoke();
            refreshTokenRepository.save(activeSession);
        }
    }

    private void validateResetRequest(ResetPasswordRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados para redefinicao de senha sao obrigatorios");
        }

        if (!StringUtils.hasText(request.token())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de recuperacao e obrigatorio");
        }

        if (!StringUtils.hasText(request.newPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha e obrigatoria");
        }

        if (request.newPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha deve ter ao menos 8 caracteres");
        }
    }

    protected String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    protected String buildResetLink(String token) {
        return resetUrlTemplate.replace("{token}", token);
    }

    private String hashToken(String token) {
        return SecurityUtils.hashSHA256(token);
    }
}