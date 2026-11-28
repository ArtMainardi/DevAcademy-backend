package com.example.trabalho.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "O comentário não pode ser vazio.")
    @Size(max = 5000, message = "O comentário deve ter no máximo 5000 caracteres.")
    private String text;
}
