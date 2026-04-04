package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.RefreshToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findBySessionId(String sessionId);

    boolean existsBySessionIdAndRevokedAtIsNullAndExpiresAtAfter(String sessionId, LocalDateTime expiresAt);

    long countByUsuarioAndRevokedAtIsNullAndExpiresAtAfter(Usuario usuario, LocalDateTime expiresAt);

    List<RefreshToken> findByUsuarioAndRevokedAtIsNullAndExpiresAtAfterOrderByCreatedAtAsc(Usuario usuario, LocalDateTime expiresAt);

    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
