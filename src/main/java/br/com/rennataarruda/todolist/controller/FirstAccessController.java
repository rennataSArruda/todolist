package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.auth.FirstAccessRequest;
import br.com.rennataarruda.todolist.service.PrimeiroAcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public/auth")
@Tag(name = "Autenticação", description = "Fluxos públicos de autenticação.")
public class FirstAccessController {

    private final PrimeiroAcessoService primeiroAcessoService;

    public FirstAccessController(PrimeiroAcessoService primeiroAcessoService) {
        this.primeiroAcessoService = primeiroAcessoService;
    }

    @Operation(
            summary = "Criar usuário no primeiro acesso",
            description = "Cria um usuário não root com o perfil padrão definido nas Configurações Gerais. Não retorna tokens; o usuário deve realizar login após o cadastro."
    )
    @PostMapping("/first-access")
    public ResponseEntity<Void> firstAccess(@RequestBody FirstAccessRequest request) {
        primeiroAcessoService.criar(request);
        return ResponseEntity.noContent().build();
    }
}
