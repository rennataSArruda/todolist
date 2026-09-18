package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.ConfiguracoesGeraisDto;
import br.com.rennataarruda.todolist.entity.ConfiguracoesGerais;
import br.com.rennataarruda.todolist.entity.Perfil;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracoesGeraisMapper {

    public ConfiguracoesGeraisDto toDto(ConfiguracoesGerais entity) {
        Perfil perfilPadraoPrimeiroAcesso = entity.getPerfilPadraoPrimeiroAcesso();

        return new ConfiguracoesGeraisDto(
                entity.getId(),
                perfilPadraoPrimeiroAcesso == null ? null : perfilPadraoPrimeiroAcesso.getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
