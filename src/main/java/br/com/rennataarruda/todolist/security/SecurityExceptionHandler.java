package br.com.rennataarruda.todolist.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SecurityExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public SecurityExceptionHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void handle(HttpServletResponse response, SecurityErrorCatalog error, String path) throws IOException {
        SecurityErrorResponse errorResponse = new SecurityErrorResponse(
                error.status().value(),
                error.code(),
                error.action(),
                error.message(),
                path
        );

        logger.debug("Security error: status={}, code={}, action={}, message={}, path={}",
                error.status().value(),
                error.code(),
                error.action(),
                error.message(),
                path);

        response.setStatus(error.status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
