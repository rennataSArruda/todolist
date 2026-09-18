package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityRequest;
import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityResponse;
import br.com.rennataarruda.todolist.service.PrimeiroAcessoDisponibilidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public/auth/first-access")
@Tag(name = "Autenticação", description = "Fluxos públicos de autenticação.")
public class FirstAccessAvailabilityController {

    private final PrimeiroAcessoDisponibilidadeService service;

    public FirstAccessAvailabilityController(PrimeiroAcessoDisponibilidadeService service) {
        this.service = service;
    }

    @Operation(
            summary = "Consultar disponibilidade de usuário ou e-mail",
            description = "Endpoint de apoio ao formulário de primeiro acesso. O cadastro continua validando duplicidades no envio final."
    )
    @PostMapping("/availability")
    public ResponseEntity<FirstAccessAvailabilityResponse> availability(
            @RequestBody FirstAccessAvailabilityRequest request
    ) {
        return ResponseEntity.ok(service.verificar(request));
    }
}
