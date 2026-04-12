package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.BlacklistedToken;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BlacklistedTokenRepository extends BaseRepository<BlacklistedToken, Long> {

    boolean existsByTokenHash(String tokenHash);

    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
