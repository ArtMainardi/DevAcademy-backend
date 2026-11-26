package com.example.trabalho.hashtag.controller;

import com.example.trabalho.hashtag.dto.HashtagResponseDTO;
import com.example.trabalho.hashtag.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping
    public ResponseEntity<List<HashtagResponseDTO>> getAllHashtags() {
        List<HashtagResponseDTO> list = hashtagService.findAll().stream()
                .map(HashtagResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HashtagResponseDTO>> searchHashtags(@RequestParam String query) {
        List<HashtagResponseDTO> list = hashtagService.searchByName(query).stream()
                .map(HashtagResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<HashtagResponseDTO>> suggestHashtags(@RequestParam(required = false) String prefix) {
        List<HashtagResponseDTO> list = hashtagService.suggestHashtags(prefix).stream()
                .map(HashtagResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}