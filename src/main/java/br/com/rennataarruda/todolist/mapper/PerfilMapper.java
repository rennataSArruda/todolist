package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.PerfilDto;
import br.com.rennataarruda.todolist.entity.Perfil;
import org.springframework.stereotype.Component;

@Component
public class PerfilMapper {

    public PerfilDto toDto(Perfil perfil) {
        return new PerfilDto(
                perfil.getId(),
                perfil.getCodigo(),
                perfil.getDescricao()
        );
    }

    public Perfil toEntity(PerfilDto dto) {
        return new Perfil(dto.codigo(), dto.descricao());
    }

    public void updateEntity(Perfil perfil, PerfilDto dto) {
        perfil.atualizar(dto.codigo(), dto.descricao());
    }
}
