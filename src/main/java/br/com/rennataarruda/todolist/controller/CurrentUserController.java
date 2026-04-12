package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.auth.AuthenticatedUserResponse;
import br.com.rennataarruda.todolist.dto.auth.ChangePasswordRequest;
import br.com.rennataarruda.todolist.security.AuthenticatedUser;
import br.com.rennataarruda.todolist.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/auth")
public class CurrentUserController {

    private final AuthService authService;

    public CurrentUserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> me(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            Authentication authentication
    ) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        AuthenticatedUserResponse response = new AuthenticatedUserResponse(
                authenticatedUser.id(),
                authenticatedUser.username(),
                authenticatedUser.name(),
                new AuthenticatedUserResponse.Perfil(
                        authenticatedUser.perfilId(),
                        authenticatedUser.perfilCodigo()
                ),
                authorities,
                authenticatedUser.root()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(authenticatedUser.username(), request);
        return ResponseEntity.noContent().build();
    }
}
