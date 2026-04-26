package br.com.rennataarruda.todolist.repository;

import br.com.rennataarruda.todolist.entity.PapelPermissao;
import br.com.rennataarruda.todolist.entity.PapelPermissaoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PapelPermissaoRepository extends JpaRepository<PapelPermissao, PapelPermissaoId> {

    boolean existsByPapelCodigoAndPermissaoCodigo(String papelCodigo, String permissaoCodigo);
}
