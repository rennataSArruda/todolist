package br.com.rennataarruda.todolist.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityExceptionHandler exceptionHandler;

    public CustomAuthenticationEntryPoint(SecurityExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        exceptionHandler.handle(response, SecurityErrorCatalog.UNAUTHORIZED, request.getRequestURI());
    }
}
