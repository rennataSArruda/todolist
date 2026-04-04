package br.com.rennataarruda.todolist.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.rennataarruda.todolist.dto.auth.AuthRequest;
import br.com.rennataarruda.todolist.dto.auth.AuthResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    public AuthService(
            UsuarioRepository usuarioRepository,
            BlacklistedTokenRepository blacklistedTokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            JwtProperties jwtProperties
    ) {
        this.usuarioRepository = usuarioRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
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

    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = getValidRefreshToken(request.refreshToken());

        revokeRefreshToken(refreshToken);

        return issueTokens(refreshToken.getUsuario());
    }

    public void logout(String authorizationHeader, RefreshTokenRequest request) {
        RefreshToken refreshToken = getValidRefreshToken(request.refreshToken());
        revokeRefreshToken(refreshToken);
        blacklistAccessToken(authorizationHeader);
    }

    private Usuario authenticate(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username e password sao obrigatorios");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas"));

        var result = BCrypt.verifyer().verify(password.toCharArray(), usuario.getPassword());
        if (!result.verified) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas");
        }

        return usuario;
    }

    private AuthResponse issueTokens(Usuario usuario) {
        enforceMaxSessions(usuario);
        String sessionId = UUID.randomUUID().toString();
        String accessToken = jwtService.generateToken(usuario.getUsername(), sessionId);
        RefreshToken refreshToken = createRefreshToken(usuario, sessionId);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtProperties.expirationMinutes() * 60
        );
    }

    private RefreshToken createRefreshToken(Usuario usuario, String sessionId) {
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
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

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
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

    private void blacklistAccessToken(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        if (!StringUtils.hasText(token)) {
            return;
        }

        if (blacklistedTokenRepository.existsByToken(token)) {
            return;
        }

        blacklistedTokenRepository.save(new BlacklistedToken(token, jwtService.extractExpiration(token)));
    }

    private String extractBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.substring("Bearer ".length());
    }
}
