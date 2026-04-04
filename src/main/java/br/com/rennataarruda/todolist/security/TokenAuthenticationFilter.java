package br.com.rennataarruda.todolist.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenAuthenticationFilter(
            JwtService jwtService,
            BlacklistedTokenRepository blacklistedTokenRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.jwtService = jwtService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
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
            unauthorized(response, "Token nao informado");
            return;
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        if (blacklistedTokenRepository.existsByToken(token)) {
            unauthorized(response, "Token invalidado");
            return;
        }

        String username;
        String sessionId;
        try {
            username = jwtService.extractUsername(token);
            sessionId = jwtService.extractSessionId(token);
        } catch (JwtException | IllegalArgumentException exception) {
            unauthorized(response, "Token invalido");
            return;
        }

        if (!StringUtils.hasText(sessionId) ||
                !refreshTokenRepository.existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(sessionId, java.time.LocalDateTime.now())) {
            unauthorized(response, "Sessao invalida");
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.NO_AUTHORITIES);

        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
