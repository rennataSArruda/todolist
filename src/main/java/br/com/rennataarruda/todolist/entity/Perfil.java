package br.com.rennataarruda.todolist.entity;

import br.com.rennataarruda.todolist.entity.commons.WithUpdatedAt;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PERFIL")
public class Perfil extends WithUpdatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "DESCRICAO", nullable = false, length = 150)
    private String descricao;

    @OneToMany(mappedBy = "perfil")
    private final Set<PerfilPapelPermissao> autorizacoes = new LinkedHashSet<>();

    public Perfil(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public void atualizar(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public void adicionarAutorizacao(Papel papel, Permissao permissao) {
        autorizacoes.add(new PerfilPapelPermissao(this, papel, permissao));
    }
}
