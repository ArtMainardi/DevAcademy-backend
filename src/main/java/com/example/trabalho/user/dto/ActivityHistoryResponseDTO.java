package com.example.trabalho.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityHistoryResponseDTO {

    private Long id;
    private String activityType;
    private String description;
    private LocalDateTime createdAt;
}