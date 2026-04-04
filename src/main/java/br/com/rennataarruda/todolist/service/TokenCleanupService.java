package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public TokenCleanupService(
            RefreshTokenRepository refreshTokenRepository,
            BlacklistedTokenRepository blacklistedTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Scheduled(cron = "${app.security.cleanup.cron}")
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiresAtBefore(now);
        blacklistedTokenRepository.deleteByExpiresAtBefore(now);
    }
}
