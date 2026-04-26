package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.TarefaCategoriaDto;
import br.com.rennataarruda.todolist.entity.TarefaCategoria;
import org.springframework.stereotype.Component;

@Component
public class TarefaCategoriaMapper {

    public TarefaCategoriaDto toDto(TarefaCategoria entity) {
        return new TarefaCategoriaDto(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TarefaCategoria toEntity(TarefaCategoriaDto dto) {
        return new TarefaCategoria(dto.nome(), dto.descricao(), dto.ativo());
    }

    public void updateEntity(TarefaCategoria entity, TarefaCategoriaDto dto) {
        entity.atualizar(dto.nome(), dto.descricao());
    }
}
