package com.example.trabalho.correction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corpo para criar uma solicitação de correção. */
public record CreateCorrectionRequestDTO(
        @NotNull(message = "O documento é obrigatório.")
        Long documentId,

        @NotBlank(message = "A descrição da correção é obrigatória.")
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
        String description) {
}
