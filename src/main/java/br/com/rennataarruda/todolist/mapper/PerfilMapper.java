package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.PerfilDto;
import br.com.rennataarruda.todolist.dto.PerfilPermissaoDto;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.PerfilPapelPermissao;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class PerfilMapper {

    public PerfilDto toDto(Perfil perfil) {
        return new PerfilDto(
                perfil.getId(),
                perfil.getCodigo(),
                perfil.getDescricao(),
                toPermissoes(perfil)
        );
    }

    public Perfil toEntity(PerfilDto dto) {
        return new Perfil(dto.codigo(), dto.descricao());
    }

    public void updateEntity(Perfil perfil, PerfilDto dto) {
        perfil.atualizar(dto.codigo(), dto.descricao());
    }

    private List<PerfilPermissaoDto> toPermissoes(Perfil perfil) {
        return perfil.getAutorizacoes()
                .stream()
                .sorted(Comparator
                        .comparing((PerfilPapelPermissao autorizacao) -> autorizacao.getPapel().getCodigo())
                        .thenComparing(autorizacao -> autorizacao.getPermissao().getCodigo()))
                .map(autorizacao -> new PerfilPermissaoDto(
                        autorizacao.getPapel().getCodigo(),
                        autorizacao.getPermissao().getCodigo()
                ))
                .toList();
    }
}
