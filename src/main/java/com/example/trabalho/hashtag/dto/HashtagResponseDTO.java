package com.example.trabalho.hashtag.dto;

import com.example.trabalho.hashtag.model.Hashtag;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class HashtagResponseDTO {
    private Long id;
    private String name;
    private String normalizedName;
    private LocalDateTime createdAt;

    public static HashtagResponseDTO fromEntity(Hashtag hashtag) {
        return HashtagResponseDTO.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .normalizedName(hashtag.getNormalizedName())
                .createdAt(hashtag.getCreatedAt())
                .build();
    }
}