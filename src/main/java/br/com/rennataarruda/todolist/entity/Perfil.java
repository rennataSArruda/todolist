package br.com.rennataarruda.todolist.entity;

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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PERFIL")
public class Perfil {

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

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

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
