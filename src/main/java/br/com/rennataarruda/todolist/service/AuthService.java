package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.AuthRequest;
import br.com.rennataarruda.todolist.dto.auth.AuthResponse;
import br.com.rennataarruda.todolist.dto.auth.ChangePasswordRequest;
import br.com.rennataarruda.todolist.dto.auth.OAuthTokenResponse;
import br.com.rennataarruda.todolist.dto.auth.RefreshTokenRequest;
import br.com.rennataarruda.todolist.entity.BlacklistedToken;
import br.com.rennataarruda.todolist.entity.RefreshToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.JwtProperties;
import br.com.rennataarruda.todolist.security.JwtService;
import br.com.rennataarruda.todolist.security.PasswordService;
import br.com.rennataarruda.todolist.security.SecurityUtils;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final PasswordService passwordService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            BlacklistedTokenRepository blacklistedTokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            JwtProperties jwtProperties,
            PasswordService passwordService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.passwordService = passwordService;
    }

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = authenticate(request.username(), request.password());
        return issueTokens(usuario);
    }

    public OAuthTokenResponse token(String username, String password) {
        Usuario usuario = authenticate(username, password);
        AuthResponse authResponse = issueTokens(usuario);
        return new OAuthTokenResponse(
                authResponse.accessToken(),
                authResponse.refreshToken(),
                authResponse.tokenType(),
                authResponse.expiresIn()
        );
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = getValidRefreshToken(request.refreshToken());

        revokeRefreshToken(refreshToken);

        return issueTokens(refreshToken.getUsuario());
    }

    public void logout(String authorizationHeader, RefreshTokenRequest request) {
        RefreshToken refreshToken = getValidRefreshToken(request.refreshToken());
        String accessToken = extractBearerTokenOrThrow(authorizationHeader);
        JwtService.AccessTokenClaims accessTokenClaims = parseAccessTokenOrThrow(accessToken);
        validateSameSession(accessTokenClaims, refreshToken);
        revokeRefreshToken(refreshToken);
        blacklistAccessToken(accessToken, accessTokenClaims.expiresAt());
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        validatePasswordChangeRequest(request);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario autenticado nao encontrado"));

        if (!passwordService.matches(request.currentPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha atual invalida");
        }

        if (passwordService.matches(request.newPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha deve ser diferente da atual");
        }

        usuario.alterarSenha(passwordService.encode(request.newPassword()));
        usuarioRepository.save(usuario);
        revokeAllActiveSessions(usuario);
    }

    private Usuario authenticate(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username e password sao obrigatorios");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas"));

        if (!passwordService.matches(password, usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas");
        }

        return usuario;
    }

    private AuthResponse issueTokens(Usuario usuario) {
        enforceMaxSessions(usuario);
        String sessionId = UUID.randomUUID().toString();
        String accessToken = jwtService.generateAccessToken(usuario.getUsername(), sessionId);
        String refreshToken = UUID.randomUUID().toString();
        createRefreshToken(usuario, sessionId, refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.expirationMinutes() * 60
        );
    }

    private RefreshToken createRefreshToken(Usuario usuario, String sessionId, String refreshTokenValue) {
        RefreshToken refreshToken = new RefreshToken(
                hashRefreshToken(refreshTokenValue),
                sessionId,
                usuario,
                LocalDateTime.now().plusDays(jwtProperties.refreshExpirationDays())
        );

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken getValidRefreshToken(String refreshTokenValue) {
        if (!StringUtils.hasText(refreshTokenValue)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token e obrigatorio");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashRefreshToken(refreshTokenValue))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }

        return refreshToken;
    }

    private void revokeRefreshToken(RefreshToken refreshToken) {
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }

    private void revokeAllActiveSessions(Usuario usuario) {
        LocalDateTime now = LocalDateTime.now();
        var activeSessions = refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(usuario, now);
        for (RefreshToken activeSession : activeSessions) {
            revokeRefreshToken(activeSession);
        }
    }

    private void enforceMaxSessions(Usuario usuario) {
        int maxSessions = jwtProperties.maxSessions();
        if (maxSessions <= 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        long activeSessions = refreshTokenRepository.countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(usuario, now);
        if (activeSessions < maxSessions) {
            return;
        }

        var sessions = refreshTokenRepository.findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(usuario, now);
        int sessionsToRevoke = (int) (activeSessions - maxSessions + 1);

        for (int index = 0; index < sessionsToRevoke && index < sessions.size(); index++) {
            revokeRefreshToken(sessions.get(index));
        }
    }

    private JwtService.AccessTokenClaims parseAccessTokenOrThrow(String accessToken) {
        try {
            return jwtService.parseAccessToken(accessToken);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido", exception);
        }
    }

    private void validateSameSession(JwtService.AccessTokenClaims accessToken, RefreshToken refreshToken) {
        if (!accessToken.sessionId().equals(refreshToken.getSessionId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessao invalida");
        }
    }

    private void blacklistAccessToken(String accessToken, LocalDateTime expiresAt) {
        String tokenHash = SecurityUtils.hashSHA256(accessToken);
        if (blacklistedTokenRepository.existsByTokenHash(tokenHash)) {
            return;
        }

        blacklistedTokenRepository.save(new BlacklistedToken(tokenHash, expiresAt));
    }

    private String extractBearerTokenOrThrow(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization bearer e obrigatorio");
        }

        return authorizationHeader.substring("Bearer ".length());
    }

    private void validatePasswordChangeRequest(ChangePasswordRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados para troca de senha sao obrigatorios");
        }

        if (!StringUtils.hasText(request.currentPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual e obrigatoria");
        }

        if (!StringUtils.hasText(request.newPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha e obrigatoria");
        }

        if (request.newPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha deve ter ao menos 8 caracteres");
        }
    }

    private String hashRefreshToken(String refreshTokenValue) {
        return SecurityUtils.hashSHA256(refreshTokenValue);
    }
}
