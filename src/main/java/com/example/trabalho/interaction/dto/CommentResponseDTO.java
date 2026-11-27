package com.example.trabalho.interaction.dto;

package com.seuprojeto.dto.interaction;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {

    private Long id;
    private String text;
    private Long authorId;
    private String authorName;
    private Long contentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
