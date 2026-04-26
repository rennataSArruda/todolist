package br.com.rennataarruda.todolist.controller.fixed;

import br.com.rennataarruda.todolist.controller.commons.AbstractTabelaFixaResource;
import br.com.rennataarruda.todolist.dto.fixed.TarefaPrioridadeDto;
import br.com.rennataarruda.todolist.service.fixed.TarefaPrioridadeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/fixed/tarefa-prioridade")
public class TarefaPrioridadeResource extends AbstractTabelaFixaResource<Long, TarefaPrioridadeDto, TarefaPrioridadeService> {

    public TarefaPrioridadeResource(TarefaPrioridadeService service) {
        super(service);
    }
}
