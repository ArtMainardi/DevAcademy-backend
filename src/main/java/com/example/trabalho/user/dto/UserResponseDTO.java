package com.example.trabalho.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String biography;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;
}