package br.com.rennataarruda.todolist.dto;

import java.util.List;

public record PerfilDto(
        Long id,
        String codigo,
        String descricao,
        List<PerfilPermissaoDto> permissoes
) {
    public PerfilDto(Long id, String codigo, String descricao) {
        this(id, codigo, descricao, null);
    }
}
