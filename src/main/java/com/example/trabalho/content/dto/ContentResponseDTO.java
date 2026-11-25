package com.example.trabalho.content.dto;

import com.example.trabalho.content.model.Content;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ContentResponseDTO {
    private Long id;
    private String title;
    private String body;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
    private Boolean published;
    private List<String> hashtags;

    public static ContentResponseDTO fromEntity(Content content) {
        List<String> tags = content.getHashtags() != null
                ? content.getHashtags().stream()
                .map(ch -> ch.getHashtag().getName())
                .collect(Collectors.toList())
                : List.of();

        return ContentResponseDTO.builder()
                .id(content.getId())
                .title(content.getTitle())
                .body(content.getBody())
                .ownerId(content.getOwner().getId())
                .ownerName(content.getOwner().getName())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .version(content.getVersion())
                .published(content.getPublished())
                .hashtags(tags)
                .build();
    }
}