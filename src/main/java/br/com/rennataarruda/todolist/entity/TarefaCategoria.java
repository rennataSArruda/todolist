package br.com.rennataarruda.todolist.entity;

import br.com.rennataarruda.todolist.entity.commons.UsuarioScopedEntity;
import br.com.rennataarruda.todolist.entity.commons.WithUpdatedAt;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

@Getter
@Entity
@Table(name = "TAREFA_CATEGORIA")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarefaCategoria extends WithUpdatedAt implements UsuarioScopedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USUARIO_ID", nullable = false)
    private Long usuarioId;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @Column(name = "COR_HEX", length = 7)
    private String corHex;

    @Column(name = "ICONE", length = 100)
    private String icone;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    public TarefaCategoria(String nome, String descricao, String corHex, String icone, Boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.corHex = corHex;
        this.icone = icone;
        this.ativo = ativo == null ? true : ativo;
    }

    @Override
    public void definirUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void atualizar(String nome, String descricao, String corHex, String icone) {
        this.nome = nome;
        this.descricao = descricao;
        this.corHex = corHex;
        this.icone = icone;
    }

    public void alternarAtivo() {
        this.ativo = !Boolean.TRUE.equals(this.ativo);
    }
}
