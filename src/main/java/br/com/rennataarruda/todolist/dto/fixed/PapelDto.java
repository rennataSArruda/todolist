package br.com.rennataarruda.todolist.dto.fixed;

import java.util.List;

public record PapelDto(
        Long id,
        String codigo,
        String descricao,
        List<PermissaoDto> permissoes
) {
}
