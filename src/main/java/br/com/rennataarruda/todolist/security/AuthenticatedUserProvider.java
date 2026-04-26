package br.com.rennataarruda.todolist.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticatedUserProvider {

    public AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw unauthorized();
        }

        if (!(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {
            throw unauthorized();
        }

        return authenticatedUser;
    }

    public Long currentUserId() {
        return currentUser().id();
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario autenticado nao encontrado");
    }
}
