package br.com.rennataarruda.todolist.controller.fixed;

import br.com.rennataarruda.todolist.controller.commons.AbstractTabelaFixaResource;
import br.com.rennataarruda.todolist.dto.fixed.PapelDto;
import br.com.rennataarruda.todolist.service.fixed.PapelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/fixed/papel")
public class PapelResource extends AbstractTabelaFixaResource<Long, PapelDto, PapelService> {

    public PapelResource(PapelService service) {
        super(service);
    }
}
