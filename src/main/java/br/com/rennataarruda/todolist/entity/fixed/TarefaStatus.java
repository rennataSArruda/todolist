package br.com.rennataarruda.todolist.entity.fixed;

import br.com.rennataarruda.todolist.entity.commons.AbstractTabelaFixaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

@Getter
@Entity
@Table(name = "TAREFA_STATUS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarefaStatus extends AbstractTabelaFixaEntity<Long> {

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo;

    public Boolean isAtivo(){
        return Boolean.TRUE.equals(this.ativo);
    }
}
