package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.auth.AuthRequest;
import br.com.rennataarruda.todolist.dto.auth.AuthResponse;
import br.com.rennataarruda.todolist.dto.auth.OAuthTokenResponse;
import br.com.rennataarruda.todolist.dto.auth.RefreshTokenRequest;
import br.com.rennataarruda.todolist.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public/auth")
@Tag(name = "Autenticacao", description = "Fluxos publicos de autenticacao. O endpoint oficial para clientes da aplicacao web e /public/auth/login.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login oficial da aplicacao", description = "Endpoint oficial para autenticacao de clientes web e integracoes da aplicacao.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @Operation(summary = "Renovar tokens", description = "Rotaciona o refresh token e devolve um novo par de tokens.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @Operation(summary = "Encerrar sessao", description = "Revoga a sessao atual e invalida o access token informado.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(authorization, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Token OAuth2 para Swagger",
            description = "Endpoint mantido para suporte ao botao Authorize do Swagger/OpenAPI. Nao e o contrato oficial para o frontend web; use /public/auth/login."
    )
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<OAuthTokenResponse> token(
            @RequestParam String username,
            @RequestParam String password
    ) {
        return ResponseEntity.ok(authService.token(username, password));
    }
}
