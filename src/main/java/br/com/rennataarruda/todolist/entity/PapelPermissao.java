package br.com.rennataarruda.todolist.entity;

import br.com.rennataarruda.todolist.entity.commons.WithCreatedAt;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PAPEL_PERMISSAO")
public class PapelPermissao extends WithCreatedAt {

    @EmbeddedId
    private PapelPermissaoId id;

    @MapsId("papelId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PAPEL_ID", nullable = false)
    private Papel papel;

    @MapsId("permissaoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERMISSAO_ID", nullable = false)
    private Permissao permissao;

    public PapelPermissao(Papel papel, Permissao permissao) {
        this.id = new PapelPermissaoId(papel.getId(), permissao.getId());
        this.papel = papel;
        this.permissao = permissao;
    }
}
