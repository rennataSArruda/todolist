package br.com.rennataarruda.todolist.entity.fixed;

import br.com.rennataarruda.todolist.entity.commons.AbstractTabelaFixaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PERMISSAO")
public class Permissao extends AbstractTabelaFixaEntity<Long> {

    public Permissao(Long id, String codigo, String descricao) {
        super(id, codigo, descricao);
    }
}
