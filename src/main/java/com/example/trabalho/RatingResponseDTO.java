package com.example.trabalho;

package com.seuprojeto.dto.interaction;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {

    private Long id;
    private Integer score;
    private Long evaluatorId;
    private Long contentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
//
