package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.auth.AuthRequest;
import br.com.rennataarruda.todolist.dto.auth.AuthResponse;
import br.com.rennataarruda.todolist.dto.auth.OAuthTokenResponse;
import br.com.rennataarruda.todolist.dto.auth.RefreshTokenRequest;
import br.com.rennataarruda.todolist.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(authorization, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<OAuthTokenResponse> token(
            @RequestParam String username,
            @RequestParam String password
    ) {
        return ResponseEntity.ok(authService.token(username, password));
    }
}
