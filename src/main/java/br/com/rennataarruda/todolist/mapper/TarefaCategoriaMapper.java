package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.TarefaCategoriaDto;
import br.com.rennataarruda.todolist.entity.TarefaCategoria;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TarefaCategoriaMapper {

    public TarefaCategoriaDto toDto(TarefaCategoria entity) {
        return new TarefaCategoriaDto(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getCorHex(),
                entity.getIcone(),
                entity.getAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TarefaCategoria toEntity(TarefaCategoriaDto dto) {
        return new TarefaCategoria(
                dto.nome(),
                dto.descricao(),
                normalizeCorHex(dto.corHex()),
                normalizeIcone(dto.icone()),
                dto.ativo()
        );
    }

    public void updateEntity(TarefaCategoria entity, TarefaCategoriaDto dto) {
        entity.atualizar(dto.nome(), dto.descricao(), normalizeCorHex(dto.corHex()), normalizeIcone(dto.icone()));
    }

    private String normalizeCorHex(String corHex) {
        if (corHex == null) {
            return null;
        }
        return corHex.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeIcone(String icone) {
        if (icone == null) {
            return null;
        }
        return icone.trim();
    }
}
