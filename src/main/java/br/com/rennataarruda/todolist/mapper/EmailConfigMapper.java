package br.com.rennataarruda.todolist.mapper;

import br.com.rennataarruda.todolist.dto.EmailConfigDto;
import br.com.rennataarruda.todolist.entity.EmailConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmailConfigMapper {

    public EmailConfigDto toDto(EmailConfig entity) {
        return new EmailConfigDto(
                entity.getId(),
                entity.getHost(),
                entity.getPort(),
                entity.getUsername(),
                null,
                entity.getFromAddress(),
                entity.getFromName(),
                entity.getAuth(),
                entity.getStartTls(),
                entity.getSsl(),
                entity.getAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public EmailConfig toEntity(EmailConfigDto dto) {
        return new EmailConfig(
                normalize(dto.host()),
                dto.port(),
                normalize(dto.username()),
                normalize(dto.password()),
                normalize(dto.fromAddress()),
                normalize(dto.fromName()),
                dto.auth(),
                dto.startTls(),
                dto.ssl(),
                dto.ativo()
        );
    }

    public void updateEntity(EmailConfig entity, EmailConfigDto dto) {
        entity.atualizar(
                normalize(dto.host()),
                dto.port(),
                normalize(dto.username()),
                resolvePassword(entity, dto),
                normalize(dto.fromAddress()),
                normalize(dto.fromName()),
                dto.auth(),
                dto.startTls(),
                dto.ssl(),
                dto.ativo()
        );
    }

    private String resolvePassword(EmailConfig entity, EmailConfigDto dto) {
        if (!StringUtils.hasText(dto.password())) {
            return entity.getPassword();
        }
        return normalize(dto.password());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}