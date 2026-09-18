package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.AtualizarConfiguracoesGeraisRequest;
import br.com.rennataarruda.todolist.dto.ConfiguracoesGeraisDto;
import br.com.rennataarruda.todolist.security.authorization.ApenasRoot;
import br.com.rennataarruda.todolist.service.ConfiguracoesGeraisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApenasRoot
@RequestMapping("api/configuracoes-gerais")
public class ConfiguracoesGeraisController {

    private final ConfiguracoesGeraisService service;

    public ConfiguracoesGeraisController(ConfiguracoesGeraisService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ConfiguracoesGeraisDto> get() {
        return ResponseEntity.ok(service.get());
    }

    @PutMapping
    public ResponseEntity<ConfiguracoesGeraisDto> atualizar(@RequestBody AtualizarConfiguracoesGeraisRequest request) {
        return ResponseEntity.ok(service.atualizarPerfilPadraoPrimeiroAcesso(
                request.perfilPadraoPrimeiroAcessoId()
        ));
    }
}
