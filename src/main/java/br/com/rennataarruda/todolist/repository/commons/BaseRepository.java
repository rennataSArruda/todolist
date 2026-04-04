package br.com.rennataarruda.todolist.repository.commons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<Entity, Id> extends JpaRepository<Entity, Id>, JpaSpecificationExecutor<Entity> {
}
