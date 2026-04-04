package br.com.rennataarruda.todolist.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Todo List API",
                version = "v1",
                description = "Documentacao da API do projeto Todo List"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                password = @OAuthFlow(
                        tokenUrl = "/public/auth/token"
                )
        )
)
public class OpenApiConfig {
}
