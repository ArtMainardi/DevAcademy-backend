package com.example.trabalho.correction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo para recusar uma solicitação: o motivo é obrigatório. */
public record RejectCorrectionDTO(
        @NotBlank(message = "O motivo da recusa é obrigatório.")
        @Size(max = 1000, message = "O motivo deve ter no máximo 1000 caracteres.")
        String reason) {
}
