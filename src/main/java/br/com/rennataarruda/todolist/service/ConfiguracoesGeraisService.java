package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.dto.ConfiguracoesGeraisDto;
import br.com.rennataarruda.todolist.entity.ConfiguracoesGerais;
import br.com.rennataarruda.todolist.entity.Perfil;
import br.com.rennataarruda.todolist.mapper.ConfiguracoesGeraisMapper;
import br.com.rennataarruda.todolist.repository.ConfiguracoesGeraisRepository;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConfiguracoesGeraisService {

    private final ConfiguracoesGeraisRepository configuracoesGeraisRepository;
    private final PerfilRepository perfilRepository;
    private final ConfiguracoesGeraisMapper mapper;

    public ConfiguracoesGeraisService(
            ConfiguracoesGeraisRepository configuracoesGeraisRepository,
            PerfilRepository perfilRepository,
            ConfiguracoesGeraisMapper mapper
    ) {
        this.configuracoesGeraisRepository = configuracoesGeraisRepository;
        this.perfilRepository = perfilRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ConfiguracoesGeraisDto get() {
        return mapper.toDto(getConfiguracoesGerais());
    }

    @Transactional
    public ConfiguracoesGeraisDto atualizarPerfilPadraoPrimeiroAcesso(Long perfilId) {
        if (perfilId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Perfil padrão de primeiro acesso é obrigatório"
            );
        }

        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Perfil padrão de primeiro acesso não encontrado"
                ));

        ConfiguracoesGerais configuracoesGerais = getConfiguracoesGerais();
        configuracoesGerais.atualizarPerfilPadraoPrimeiroAcesso(perfil);

        return mapper.toDto(configuracoesGeraisRepository.save(configuracoesGerais));
    }

    @Transactional(readOnly = true)
    public Perfil getPerfilPadraoPrimeiroAcesso() {
        Perfil perfil = getConfiguracoesGerais().getPerfilPadraoPrimeiroAcesso();

        if (perfil == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Perfil padrão de primeiro acesso não configurado"
            );
        }

        return perfil;
    }

    private ConfiguracoesGerais getConfiguracoesGerais() {
        return configuracoesGeraisRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Configurações gerais não inicializadas"
                ));
    }
}
