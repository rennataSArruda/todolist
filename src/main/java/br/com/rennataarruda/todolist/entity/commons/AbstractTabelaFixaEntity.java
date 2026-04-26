package br.com.rennataarruda.todolist.entity.commons;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTabelaFixaEntity<ID extends Serializable> {

    @Id
    @Column(name = "ID", nullable = false)
    private ID id;

    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @Column(name = "DESCRICAO", nullable = false, length = 150)
    private String descricao;

    protected AbstractTabelaFixaEntity(ID id, String codigo, String descricao) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
