package br.com.rennataarruda.todolist.dto;

import java.time.LocalDateTime;

public record ConfiguracoesGeraisDto(
        Long id,
        Long perfilPadraoPrimeiroAcessoId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
