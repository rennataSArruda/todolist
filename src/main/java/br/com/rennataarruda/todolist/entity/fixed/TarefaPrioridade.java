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
@Table(name = "TAREFA_PRIORIDADE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarefaPrioridade extends AbstractTabelaFixaEntity<Long> {

    @Column(name = "ORDEM", nullable = false)
    private Long ordem;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo;
}
