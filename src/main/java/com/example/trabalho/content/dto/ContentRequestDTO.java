package com.example.trabalho.content.dto;

import lombok.Data;
import java.util.List;

@Data
public class ContentRequestDTO {
    private String title;
    private String body;
    private Boolean published;
    private List<String> hashtags;
    private String changeDescription;
    private Long version;
}