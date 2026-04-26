package br.com.rennataarruda.todolist.controller.fixed;

import br.com.rennataarruda.todolist.controller.commons.AbstractTabelaFixaResource;
import br.com.rennataarruda.todolist.dto.fixed.TarefaStatusDto;
import br.com.rennataarruda.todolist.service.fixed.TarefaStatusService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/fixed/tarefa-status")
public class TarefaStatusResource extends AbstractTabelaFixaResource<Long, TarefaStatusDto, TarefaStatusService> {

    public TarefaStatusResource(TarefaStatusService service) {
        super(service);
    }
}
