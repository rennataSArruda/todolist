package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.PasswordResetToken;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends BaseRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    List<PasswordResetToken> findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(Usuario usuario, LocalDateTime expiresAt);

    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
