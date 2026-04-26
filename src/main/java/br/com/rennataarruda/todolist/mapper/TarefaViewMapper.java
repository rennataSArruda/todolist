package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.view.TarefaViewDto;
import br.com.rennataarruda.todolist.entity.view.TarefaView;
import org.springframework.stereotype.Component;

@Component
public class TarefaViewMapper {

    public TarefaViewDto toDto(TarefaView entity) {
        return new TarefaViewDto(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getCategoriaId(),
                entity.getCategoriaNome(),
                entity.getCategoriaDescricao(),
                entity.getStatusId(),
                entity.getStatusCodigo(),
                entity.getStatusDescricao(),
                entity.getPrioridadeId(),
                entity.getPrioridadeCodigo(),
                entity.getPrioridadeDescricao(),
                entity.getPrioridadeOrdem(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getDataConclusao(),
                TarefaTempoMapper.calcularEmMinutos(entity.getDataInicio(), entity.getDataFim()),
                TarefaTempoMapper.calcularEmMinutos(entity.getDataInicio(), entity.getDataConclusao()),
                entity.getPosicao(),
                entity.getImportante(),
                entity.getAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
