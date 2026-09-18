package br.com.rennataarruda.todolist.service.email;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailTemplateService {

    public String passwordReset(String resetLink) {
        String template = loadTemplate("templates/password-reset.html");

        return template.replace("{{RESET_LINK}}", resetLink);
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);

            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível carregar o template de e-mail: " + path,
                    exception
            );
        }
    }
}
