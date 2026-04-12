package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final UserAuthorityService userAuthorityService;
    private final SecurityExceptionHandler exceptionHandler;

    public TokenAuthenticationFilter(
            JwtService jwtService,
            BlacklistedTokenRepository blacklistedTokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            UsuarioRepository usuarioRepository,
            UserAuthorityService userAuthorityService,
            SecurityExceptionHandler exceptionHandler
    ) {
        this.jwtService = jwtService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.userAuthorityService = userAuthorityService;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            exceptionHandler.handle(response, SecurityErrorCatalog.TOKEN_MISSING, request.getRequestURI());
            return;
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        if (blacklistedTokenRepository.existsByTokenHash(SecurityUtils.hashSHA256(token))) {
            logger.warn("Access attempt with blacklisted token");
            exceptionHandler.handle(response, SecurityErrorCatalog.TOKEN_INVALIDATED, request.getRequestURI());
            return;
        }

        JwtService.AccessTokenClaims accessToken;
        try {
            accessToken = jwtService.parseAccessToken(token);
        } catch (ExpiredJwtException exception) {
            logger.info("Access attempt with expired token: {}", exception.getMessage());
            exceptionHandler.handle(response, SecurityErrorCatalog.TOKEN_EXPIRED, request.getRequestURI());
            return;
        } catch (JwtException | IllegalArgumentException exception) {
            logger.warn("Access attempt with invalid token: {}", exception.getMessage());
            exceptionHandler.handle(response, SecurityErrorCatalog.TOKEN_INVALID, request.getRequestURI());
            return;
        }

        if (!refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(
                accessToken.sessionId(),
                LocalDateTime.now()
        )) {
            logger.warn("Access attempt with invalid session: sid={}", accessToken.sessionId());
            exceptionHandler.handle(response, SecurityErrorCatalog.SESSION_INVALID, request.getRequestURI());
            return;
        }

        Usuario usuario = usuarioRepository.findWithAuthorizationByUsername(accessToken.username())
                .orElse(null);
        if (usuario == null || usuario.getPerfil() == null) {
            logger.warn("Access attempt by user without profile: username={}", accessToken.username());
            exceptionHandler.handle(response, SecurityErrorCatalog.USER_WITHOUT_PROFILE, request.getRequestURI());
            return;
        }

        AuthenticatedUser principal = new AuthenticatedUser(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getName(),
                usuario.isRoot(),
                usuario.getPerfil().getId(),
                usuario.getPerfil().getCodigo(),
                accessToken.sessionId()
        );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, userAuthorityService.getAuthorities(usuario));

        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
