package com.example.trabalho;

import jakarta.validation.constraints.Size;

/** Corpo opcional para aceitar, adiar, retomar ou concluir (observação livre). */
public record CorrectionActionDTO(
        @Size(max = 1000, message = "A observação deve ter no máximo 1000 caracteres.")
        String note) {
}
