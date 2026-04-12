package br.com.rennataarruda.todolist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PERFIL_PAPEL_PERMISSAO")
public class PerfilPapelPermissao {

    @EmbeddedId
    private PerfilPapelPermissaoId id;

    @MapsId("perfilId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERFIL_ID", nullable = false)
    private Perfil perfil;

    @MapsId("papelId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PAPEL_ID", nullable = false)
    private Papel papel;

    @MapsId("permissaoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERMISSAO_ID", nullable = false)
    private Permissao permissao;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PerfilPapelPermissao(Perfil perfil, Papel papel, Permissao permissao) {
        this.id = new PerfilPapelPermissaoId(perfil.getId(), papel.getId(), permissao.getId());
        this.perfil = perfil;
        this.papel = papel;
        this.permissao = permissao;
    }

    public String getAuthority() {
        return papel.getCodigo() + "_" + permissao.getCodigo();
    }
}
