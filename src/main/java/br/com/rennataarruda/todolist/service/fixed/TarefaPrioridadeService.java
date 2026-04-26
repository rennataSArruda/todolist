package br.com.rennataarruda.todolist.service.fixed;

import br.com.rennataarruda.todolist.dto.fixed.TarefaPrioridadeDto;
import br.com.rennataarruda.todolist.entity.fixed.TarefaPrioridade;
import br.com.rennataarruda.todolist.repository.fixed.TarefaPrioridadeRepository;
import br.com.rennataarruda.todolist.service.commons.AbstractTabelaFixaService;
import org.springframework.stereotype.Service;

@Service
public class TarefaPrioridadeService extends AbstractTabelaFixaService<TarefaPrioridade, Long, TarefaPrioridadeDto> {

    public TarefaPrioridadeService(TarefaPrioridadeRepository repository) {
        super(repository);
    }

    @Override
    protected TarefaPrioridadeDto toDto(TarefaPrioridade entity) {
        return new TarefaPrioridadeDto(
                entity.getId(),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getOrdem(),
                entity.getAtivo()
        );
    }

    @Override
    protected String notFoundMessage() {
        return "Prioridade da tarefa nao encontrada";
    }
}
