package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.PasswordResetTokenRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TokenCleanupServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private TokenCleanupService service;

    @BeforeEach
    void setUp() {
        service = new TokenCleanupService(
                refreshTokenRepository,
                blacklistedTokenRepository,
                passwordResetTokenRepository
        );
    }

    @Test
    void shouldDeleteExpiredTokensFromAllTokenRepositories() {
        service.cleanupExpiredTokens();

        verify(refreshTokenRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));
        verify(blacklistedTokenRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));
        verify(passwordResetTokenRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));
        verifyNoMoreInteractions(refreshTokenRepository, blacklistedTokenRepository, passwordResetTokenRepository);
    }
}
