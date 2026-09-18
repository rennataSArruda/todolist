package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityRequest;
import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityResponse;
import br.com.rennataarruda.todolist.dto.auth.FirstAccessAvailabilityType;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PrimeiroAcessoDisponibilidadeService {

    private final UsuarioRepository usuarioRepository;

    public PrimeiroAcessoDisponibilidadeService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public FirstAccessAvailabilityResponse verificar(FirstAccessAvailabilityRequest request) {
        validateRequest(request);

        boolean exists = switch (request.type()) {
            case USERNAME -> usuarioRepository.existsByUsername(request.value());
            case EMAIL -> usuarioRepository.existsByEmail(request.value());
        };

        return new FirstAccessAvailabilityResponse(!exists);
    }

    private void validateRequest(FirstAccessAvailabilityRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados de disponibilidade são obrigatórios");
        }

        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de disponibilidade é obrigatório");
        }

        if (!StringUtils.hasText(request.value())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor para verificação de disponibilidade é obrigatório");
        }
    }
}
