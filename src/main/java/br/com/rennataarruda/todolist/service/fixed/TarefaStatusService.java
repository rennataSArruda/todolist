package br.com.rennataarruda.todolist.service.fixed;

import br.com.rennataarruda.todolist.dto.fixed.TarefaStatusDto;
import br.com.rennataarruda.todolist.entity.fixed.TarefaStatus;
import br.com.rennataarruda.todolist.repository.fixed.TarefaStatusRepository;
import br.com.rennataarruda.todolist.service.commons.AbstractTabelaFixaService;
import org.springframework.stereotype.Service;

@Service
public class TarefaStatusService extends AbstractTabelaFixaService<TarefaStatus, Long, TarefaStatusDto> {

    public TarefaStatusService(TarefaStatusRepository repository) {
        super(repository);
    }

    @Override
    protected TarefaStatusDto toDto(TarefaStatus entity) {
        return new TarefaStatusDto(
                entity.getId(),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getAtivo()
        );
    }

    @Override
    protected String notFoundMessage() {
        return "Status da tarefa nao encontrado";
    }
}
