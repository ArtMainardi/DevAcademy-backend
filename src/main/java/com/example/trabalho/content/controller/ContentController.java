package com.example.trabalho.content.controller;

import com.example.trabalho.content.dto.ContentChangeDTO;
import com.example.trabalho.content.dto.ContentRequestDTO;
import com.example.trabalho.content.dto.ContentResponseDTO;
import com.example.trabalho.content.model.Content;
import com.example.trabalho.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    public ResponseEntity<ContentResponseDTO> createContent(
            @RequestBody ContentRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        Content created = contentService.createContent(
                request.getTitle(),
                request.getBody(),
                request.getPublished(),
                request.getHashtags(),
                userId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ContentResponseDTO.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> updateContent(
            @PathVariable Long id,
            @RequestBody ContentRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        Content updated = contentService.updateContent(
                id,
                request.getTitle(),
                request.getBody(),
                request.getChangeDescription(),
                userId,
                request.getVersion()
        );
        return ResponseEntity.ok(ContentResponseDTO.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        contentService.deleteContent(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> getContentById(@PathVariable Long id) {
        Content content = contentService.getContentById(id);
        return ResponseEntity.ok(ContentResponseDTO.fromEntity(content));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ContentResponseDTO>> getMyContents(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ContentResponseDTO> page = contentService.listUserOwnContents(userId, pageable)
                .map(ContentResponseDTO::fromEntity);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/explore")
    public ResponseEntity<Page<ContentResponseDTO>> exploreContents(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ContentResponseDTO> page = contentService.explorePublishedContents(pageable)
                .map(ContentResponseDTO::fromEntity);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ContentResponseDTO>> searchContents(
            @RequestParam String keyword,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ContentResponseDTO> page = contentService.searchByKeyword(keyword, pageable)
                .map(ContentResponseDTO::fromEntity);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search/hashtags")
    public ResponseEntity<Page<ContentResponseDTO>> searchByHashtags(
            @RequestParam List<String> tags,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ContentResponseDTO> page = contentService.searchByHashtags(tags, pageable)
                .map(ContentResponseDTO::fromEntity);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/{id}/hashtags")
    public ResponseEntity<Void> addHashtag(
            @PathVariable Long id,
            @RequestParam String tag,
            @RequestHeader("X-User-Id") Long userId) {
        contentService.addHashtagToContent(id, tag, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/hashtags/{hashtagId}")
    public ResponseEntity<Void> removeHashtag(
            @PathVariable Long id,
            @PathVariable Long hashtagId,
            @RequestHeader("X-User-Id") Long userId) {
        contentService.removeHashtagFromContent(id, hashtagId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ContentChangeDTO>> getChangeHistory(@PathVariable Long id) {
        List<ContentChangeDTO> history = contentService.getChangeHistory(id).stream()
                .map(ContentChangeDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }
}