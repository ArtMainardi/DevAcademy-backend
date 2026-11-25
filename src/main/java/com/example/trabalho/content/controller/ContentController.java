package com.example.trabalho.content.controller;

import com.example.trabalho.content.dto.ContentChangeDTO;
import com.example.trabalho.content.dto.ContentRequestDTO;
import com.example.trabalho.content.dto.ContentResponseDTO;
import com.example.trabalho.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
        ContentResponseDTO created = contentService.createContent(
                request.getTitle(),
                request.getBody(),
                request.getPublished(),
                request.getHashtags(),
                userId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> updateContent(
            @PathVariable Long id,
            @RequestBody ContentRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        ContentResponseDTO updated = contentService.updateContent(
                id,
                request.getTitle(),
                request.getBody(),
                request.getChangeDescription(),
                userId,
                request.getVersion()
        );
        return ResponseEntity.ok(updated);
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
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ContentResponseDTO>> getMyContents(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.listUserOwnContents(userId, pageable));
    }

    @GetMapping("/explore")
    public ResponseEntity<Page<ContentResponseDTO>> exploreContents(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.explorePublishedContents(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ContentResponseDTO>> searchContents(
            @RequestParam String keyword,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.searchByKeyword(keyword, pageable));
    }

    @GetMapping("/search/hashtags")
    public ResponseEntity<Page<ContentResponseDTO>> searchByHashtags(
            @RequestParam List<String> tags,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.searchByHashtags(tags, pageable));
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