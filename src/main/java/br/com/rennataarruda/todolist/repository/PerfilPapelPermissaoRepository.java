package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.PerfilPapelPermissao;
import br.com.rennataarruda.todolist.entity.PerfilPapelPermissaoId;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilPapelPermissaoRepository extends BaseRepository<PerfilPapelPermissao, PerfilPapelPermissaoId> {

    @Modifying(flushAutomatically = true)
    @Query("delete from PerfilPapelPermissao autorizacao where autorizacao.perfil.id = :perfilId")
    void deleteByPerfilId(@Param("perfilId") Long perfilId);
}
