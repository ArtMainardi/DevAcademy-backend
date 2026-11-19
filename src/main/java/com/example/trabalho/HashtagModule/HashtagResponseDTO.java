package com.example.trabalho.HashtagModule;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagResponseDTO {

    private Long id;
    private String name;
    private String normalizedName;
    private LocalDateTime createdAt;
}
