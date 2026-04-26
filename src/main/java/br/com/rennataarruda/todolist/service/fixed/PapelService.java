package br.com.rennataarruda.todolist.service.fixed;

import br.com.rennataarruda.todolist.dto.fixed.PapelDto;
import br.com.rennataarruda.todolist.dto.fixed.PermissaoDto;
import br.com.rennataarruda.todolist.entity.fixed.Papel;
import br.com.rennataarruda.todolist.repository.PapelRepository;
import br.com.rennataarruda.todolist.service.commons.AbstractTabelaFixaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PapelService extends AbstractTabelaFixaService<Papel, Long, PapelDto> {

    public PapelService(PapelRepository repository) {
        super(repository);
    }

    @Override
    protected PapelDto toDto(Papel entity) {
        List<PermissaoDto> permissoes = List.of();
        if (entity.getPermissoes() != null) {
            permissoes = entity.getPermissoes().stream()
                    .map(pp -> new PermissaoDto(
                            pp.getPermissao().getId(),
                            pp.getPermissao().getCodigo(),
                            pp.getPermissao().getDescricao()
                    ))
                    .toList();
        }

        return new PapelDto(
                entity.getId(),
                entity.getCodigo(),
                entity.getDescricao(),
                permissoes
        );
    }

    @Override
    protected String notFoundMessage() {
        return "Papel não encontrado";
    }
}
