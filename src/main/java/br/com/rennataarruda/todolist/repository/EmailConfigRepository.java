package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.EmailConfig;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailConfigRepository extends BaseRepository<EmailConfig, Long> {

    Optional<EmailConfig> findFirstByAtivoTrueOrderByIdDesc();

    List<EmailConfig> findByAtivoTrue();
}