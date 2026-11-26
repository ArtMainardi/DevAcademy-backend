package com.example.trabalho.content.dto;

import com.example.trabalho.content.model.ContentChange;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ContentChangeDTO {
    private Long id;
    private String description;
    private LocalDateTime changedAt;
    private Long changedById;
    private String changedByName;

    public static ContentChangeDTO fromEntity(ContentChange change) {
        return ContentChangeDTO.builder()
                .id(change.getId())
                .description(change.getDescription())
                .changedAt(change.getChangedAt())
                .changedById(change.getChangedBy().getId())
                .changedByName(change.getChangedBy().getName())
                .build();
    }
}