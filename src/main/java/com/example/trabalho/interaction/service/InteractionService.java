package com.example.trabalho.interaction.service;

import com.example.trabalho.content.model.Content;
import com.example.trabalho.content.repository.ContentRepository;
import com.example.trabalho.interaction.dto.CommentResponseDTO;
import com.example.trabalho.interaction.module.Comment;
import com.example.trabalho.interaction.module.ContentRating;
import com.example.trabalho.interaction.module.Like;
import com.example.trabalho.interaction.repository.CommentRepository;
import com.example.trabalho.interaction.repository.ContentRatingRepository;
import com.example.trabalho.interaction.repository.LikeRepository;
import com.example.trabalho.user.model.User;
import com.example.trabalho.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InteractionService {

    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ContentRatingRepository contentRatingRepository;

    // =====================================================
    // CURTIDAS
    // =====================================================

    // Curtir documento
    @Transactional
    public Like likeDocument(Long userId, Long contentId) {

        User user = findUser(userId);
        Content content = findContent(contentId);

        if (likeRepository.existsByUser_IdAndContent_Id(
                userId, contentId)) {
            throw new IllegalStateException(
                    "Você já curtiu este documento."
            );
        }

        Like like = Like.builder()
                .user(user)
                .content(content)
                .build();

        return likeRepository.save(like);
    }

    // Remover curtida
    @Transactional
    public void unlikeDocument(Long userId, Long contentId) {

        if (!likeRepository.existsByUser_IdAndContent_Id(
                userId, contentId)) {
            throw new EntityNotFoundException(
                    "Curtida não encontrada."
            );
        }

        likeRepository.deleteByUser_IdAndContent_Id(
                userId, contentId
        );
    }

    // Consultar quantidade de curtidas
    @Transactional(readOnly = true)
    public long countLikes(Long contentId) {

        if (!contentRepository.existsById(contentId)) {
            throw new EntityNotFoundException(
                    "Documento não encontrado."
            );
        }

        return likeRepository.countByContent_Id(contentId);
    }

    // =====================================================
    // COMENTÁRIOS
    // =====================================================

    // Publicar comentário
    @Transactional
    public Comment createComment(
            Long userId,
            Long contentId,
            String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "O comentário não pode ser vazio."
            );
        }

        User user = findUser(userId);
        Content content = findContent(contentId);

        Comment comment = Comment.builder()
                .text(text.trim())
                .author(user)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    // Atualizar comentário
    @Transactional
    public Comment updateComment(
            Long userId,
            Long commentId,
            String newText) {

        Comment comment = findComment(commentId);

        validateCommentAuthor(comment, userId);

        comment.updateText(newText);

        return commentRepository.save(comment);
    }

    // Excluir comentário
    @Transactional
    public void deleteComment(
            Long userId,
            Long commentId) {

        Comment comment = findComment(commentId);

        validateCommentAuthor(comment, userId);

        commentRepository.delete(comment);
    }

    // Consultar comentários de um documento
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getComments(Long contentId) {
        return commentRepository.findByContentIdOrderByCreatedAtDesc(contentId)
                .stream()
                .map(comment -> CommentResponseDTO.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .authorId(comment.getAuthor().getId())
                        .authorName(comment.getAuthor().getName())
                        .contentId(comment.getContent().getId())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .toList();
    }

    // =====================================================
    // AVALIAÇÕES
    // =====================================================

    // Avaliar documento ou retornar avaliação existente
    @Transactional
    public ContentRating rateDocument(
            Long userId,
            Long contentId,
            Integer score) {

        validateScore(score);

        User user = findUser(userId);
        Content content = findContent(contentId);

        validateNotOwner(user, content);

        // Verifica se o usuário já avaliou o documento
        ContentRating existingRating =
                contentRatingRepository
                        .findByEvaluatorIdAndContentId(
                                userId, contentId
                        )
                        .orElse(null);

        // Atualiza avaliação existente
        if (existingRating != null) {
            existingRating.updateScore(score);

            return contentRatingRepository.save(
                    existingRating
            );
        }

        // Cria nova avaliação
        ContentRating rating = ContentRating.builder()
                .score(score)
                .evaluator(user)
                .content(content)
                .build();

        return contentRatingRepository.save(rating);
    }

    // Atualizar avaliação existente
    @Transactional
    public ContentRating updateRating(
            Long userId,
            Long contentId,
            Integer newScore) {

        validateScore(newScore);

        ContentRating rating =
                contentRatingRepository
                        .findByEvaluatorIdAndContentId(
                                userId, contentId
                        )
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Avaliação não encontrada."
                        ));

        if (!rating.isEvaluator(userId)) {
            throw new SecurityException(
                    "Você não tem permissão para atualizar esta avaliação."
            );
        }

        rating.updateScore(newScore);

        return contentRatingRepository.save(rating);
    }

    // Consultar média das avaliações
    @Transactional(readOnly = true)
    public Double getAverageRating(Long contentId) {

        if (!contentRepository.existsById(contentId)) {
            throw new EntityNotFoundException(
                    "Documento não encontrado."
            );
        }

        Double average =
                contentRatingRepository
                        .calculateAverageByContentId(contentId);

        return average != null ? average : 0.0;
    }

    // Consultar quantidade de avaliações
    @Transactional(readOnly = true)
    public long countRatings(Long contentId) {

        if (!contentRepository.existsById(contentId)) {
            throw new EntityNotFoundException(
                    "Documento não encontrado."
            );
        }

        return contentRatingRepository.countByContentId(
                contentId
        );
    }

    // Consultar avaliações de um documento
    @Transactional(readOnly = true)
    public List<ContentRating> getRatings(Long contentId) {
        if (!contentRepository.existsById(contentId)) {
            throw new EntityNotFoundException(
                    "Documento não encontrado."
            );
        }

        return contentRatingRepository.findByContentIdOrderByCreatedAtDesc(contentId);
    }

    // =====================================================
    // VALIDAÇÕES E MÉTODOS AUXILIARES
    // =====================================================

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado."
                ));
    }

    private Content findContent(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Documento não encontrado."
                ));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Comentário não encontrado."
                ));
    }

    // Validar autoria do comentário
    private void validateCommentAuthor(
            Comment comment,
            Long userId) {

        if (!comment.isAuthor(userId)) {
            throw new SecurityException(
                    "Você não tem permissão para modificar este comentário."
            );
        }
    }

    // Impedir que o proprietário avalie o próprio documento
    private void validateNotOwner(
            User user,
            Content content) {

        if (content.getOwner() != null &&
                content.getOwner().getId().equals(user.getId())) {

            throw new IllegalArgumentException(
                    "Você não pode avaliar seu próprio documento."
            );
        }
    }

    // Validar nota
    private void validateScore(Integer score) {

        if (score == null || score < 1 || score > 5) {
            throw new IllegalArgumentException(
                    "A nota deve estar entre 1 e 5."
            );
        }
    }
}
