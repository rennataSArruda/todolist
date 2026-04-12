package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.UsuarioDto;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDto toDto(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getName(),
                null
        );
    }

    public Usuario toEntity(UsuarioDto dto, String encodedPassword, Perfil perfil) {
        return new Usuario(dto.username(), dto.name(), encodedPassword, perfil);
    }

    public void updateEntity(Usuario usuario, UsuarioDto dto) {
        usuario.atualizar(dto.username(), dto.name());
    }
}
