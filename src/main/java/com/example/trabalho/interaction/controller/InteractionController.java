
package com.example.trabalho.interaction.controller;

import com.example.trabalho.interaction.dto.*;
import com.seuprojeto.dto.interaction.*;
import com.seuprojeto.model.Comment;
import com.seuprojeto.model.ContentRating;
import com.seuprojeto.model.Like;
import com.seuprojeto.security.CustomUserDetails;
import com.seuprojeto.service.InteractionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    // =====================================================
    // CURTIDAS
    // =====================================================

    // Curtir documento
    @PostMapping("/contents/{contentId}/likes")
    public ResponseEntity<InteractionMessageDTO> likeDocument(
            @PathVariable Long contentId,
            @AuthenticationPrincipal CustomUserDetails user) {

        interactionService.likeDocument(
                user.getId(),
                contentId
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InteractionMessageDTO.builder()
                        .message("Documento curtido com sucesso.")
                        .build());
    }

    // Remover curtida
    @DeleteMapping("/contents/{contentId}/likes")
    public ResponseEntity<Void> unlikeDocument(
            @PathVariable Long contentId,
            @AuthenticationPrincipal CustomUserDetails user) {

        interactionService.unlikeDocument(
                user.getId(),
                contentId
        );

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // COMENTÁRIOS
    // =====================================================

    // Criar comentário
    @PostMapping("/contents/{contentId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long contentId,
            @Valid @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        Comment comment = interactionService.createComment(
                user.getId(),
                contentId,
                request.getText()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toCommentDTO(comment));
    }

    // Atualizar comentário
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        Comment comment = interactionService.updateComment(
                user.getId(),
                commentId,
                request.getText()
        );

        return ResponseEntity.ok(toCommentDTO(comment));
    }

    // Excluir comentário
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user) {

        interactionService.deleteComment(
                user.getId(),
                commentId
        );

        return ResponseEntity.noContent().build();
    }

    // Consultar comentários de um documento
    @GetMapping("/contents/{contentId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable Long contentId) {

        List<CommentResponseDTO> comments =
                interactionService.getComments(contentId)
                        .stream()
                        .map(this::toCommentDTO)
                        .toList();

        return ResponseEntity.ok(comments);
    }

    // =====================================================
    // AVALIAÇÕES
    // =====================================================

    // Criar avaliação ou atualizar avaliação existente
    @PostMapping("/contents/{contentId}/ratings")
    public ResponseEntity<RatingResponseDTO> rateDocument(
            @PathVariable Long contentId,
            @Valid @RequestBody RatingRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        ContentRating rating =
                interactionService.rateDocument(
                        user.getId(),
                        contentId,
                        request.getScore()
                );

        return ResponseEntity.ok(toRatingDTO(rating));
    }

    // Atualizar avaliação
    @PutMapping("/contents/{contentId}/ratings")
    public ResponseEntity<RatingResponseDTO> updateRating(
            @PathVariable Long contentId,
            @Valid @RequestBody RatingRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        ContentRating rating =
                interactionService.updateRating(
                        user.getId(),
                        contentId,
                        request.getScore()
                );

        return ResponseEntity.ok(toRatingDTO(rating));
    }

    // Consultar avaliações de um documento
    @GetMapping("/contents/{contentId}/ratings")
    public ResponseEntity<List<RatingResponseDTO>> getRatings(
            @PathVariable Long contentId) {

        List<RatingResponseDTO> ratings =
                interactionService.getRatings(contentId)
                        .stream()
                        .map(this::toRatingDTO)
                        .toList();

        return ResponseEntity.ok(ratings);
    }

    // =====================================================
    // CONVERSORES DE DTO
    // =====================================================

    private CommentResponseDTO toCommentDTO(Comment comment) {

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .contentId(comment.getContent().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private RatingResponseDTO toRatingDTO(
            ContentRating rating) {

        return RatingResponseDTO.builder()
                .id(rating.getId())
                .score(rating.getScore())
                .evaluatorId(rating.getEvaluator().getId())
                .contentId(rating.getContent().getId())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
