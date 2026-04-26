package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.TarefaDto;
import br.com.rennataarruda.todolist.entity.Tarefa;
import org.springframework.stereotype.Component;

@Component
public class TarefaMapper {

    public TarefaDto toDto(Tarefa entity) {
        return new TarefaDto(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getCategoriaId(),
                entity.getStatusId(),
                entity.getPrioridadeId(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getDataConclusao(),
                TarefaTempoMapper.calcularEmMinutos(entity.getDataInicio(), entity.getDataFim()),
                TarefaTempoMapper.calcularEmMinutos(entity.getDataInicio(), entity.getDataConclusao()),
                entity.getPosicao(),
                entity.getAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Tarefa toEntity(TarefaDto dto) {
        return new Tarefa(
                dto.categoriaId(),
                dto.statusId(),
                dto.prioridadeId(),
                dto.titulo(),
                dto.descricao(),
                dto.dataInicio(),
                dto.dataFim(),
                dto.dataConclusao(),
                dto.posicao()
        );
    }

    public void updateEntity(Tarefa entity, TarefaDto dto) {
        entity.atualizar(
                dto.categoriaId(),
                dto.statusId(),
                dto.prioridadeId(),
                dto.titulo(),
                dto.descricao(),
                dto.dataInicio(),
                dto.dataFim(),
                dto.dataConclusao(),
                dto.posicao()
        );
    }
}
