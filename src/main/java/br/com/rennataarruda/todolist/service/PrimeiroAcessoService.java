package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.FirstAccessRequest;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.entity.Usuario;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import br.com.rennataarruda.todolist.security.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PrimeiroAcessoService {

    private final UsuarioRepository usuarioRepository;
    private final ConfiguracoesGeraisService configuracoesGeraisService;
    private final PasswordService passwordService;

    public PrimeiroAcessoService(
            UsuarioRepository usuarioRepository,
            ConfiguracoesGeraisService configuracoesGeraisService,
            PasswordService passwordService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.configuracoesGeraisService = configuracoesGeraisService;
        this.passwordService = passwordService;
    }

    @Transactional
    public void criar(FirstAccessRequest request) {
        validateRequest(request);

        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já cadastrado");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        Perfil perfilPadrao = configuracoesGeraisService.getPerfilPadraoPrimeiroAcesso();
        Usuario usuario = new Usuario(
                request.username(),
                request.email(),
                request.name(),
                passwordService.encode(request.password()),
                Boolean.FALSE,
                perfilPadrao
        );

        usuarioRepository.save(usuario);
    }

    private void validateRequest(FirstAccessRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados de primeiro acesso são obrigatórios");
        }

        if (!StringUtils.hasText(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário é obrigatório");
        }

        if (!StringUtils.hasText(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail é obrigatório");
        }

        if (!StringUtils.hasText(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha é obrigatória");
        }

        if (request.password().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha deve ter pelo menos 8 caracteres");
        }
    }
}
