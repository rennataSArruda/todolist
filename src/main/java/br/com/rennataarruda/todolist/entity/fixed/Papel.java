package br.com.rennataarruda.todolist.entity.fixed;

import br.com.rennataarruda.todolist.entity.PapelPermissao;
import br.com.rennataarruda.todolist.entity.commons.AbstractTabelaFixaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PAPEL")
public class Papel extends AbstractTabelaFixaEntity<Long> {

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "papel", fetch = FetchType.LAZY)
    private List<PapelPermissao> permissoes;

    public Papel(Long id, String codigo, String descricao) {
        super(id, codigo, descricao);
    }

    public Papel(String codigo, String descricao) {
        super(null, codigo, descricao);
    }
}
