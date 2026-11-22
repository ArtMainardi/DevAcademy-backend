package com.example.trabalho.HashtagModule;

import com.seuprojeto.dto.hashtag.HashtagResponseDTO;
import com.seuprojeto.model.Hashtag;
import com.seuprojeto.service.HashtagService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    // Pesquisar hashtags pelo nome
    @GetMapping("/search")
    public ResponseEntity<List<HashtagResponseDTO>> search(
            @RequestParam String name) {

        List<HashtagResponseDTO> hashtags = hashtagService
                .searchByName(name)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(hashtags);
    }

    // Consultar se uma hashtag existe
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(
            @RequestParam String name) {

        boolean exists = hashtagService.exists(name);

        return ResponseEntity.ok(exists);
    }

    // Sugerir hashtags cadastradas
    @GetMapping("/suggestions")
    public ResponseEntity<List<HashtagResponseDTO>> suggest(
            @RequestParam(required = false) String term) {

        List<HashtagResponseDTO> suggestions = hashtagService
                .suggestHashtags(term)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(suggestions);
    }

    // Converter entidade para DTO
    private HashtagResponseDTO toDTO(Hashtag hashtag) {
        return HashtagResponseDTO.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .normalizedName(hashtag.getNormalizedName())
                .createdAt(hashtag.getCreatedAt())
                .build();
    }
}
