package com.example.trabalho.interaction.repository;

import com.seuprojeto.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Listar comentários de um documento por data de criação
    List<Comment> findByContentIdOrderByCreatedAtDesc(Long contentId);

    // Listar comentários de um documento com paginação
    Page<Comment> findByContentIdOrderByCreatedAtDesc(
            Long contentId,
            Pageable pageable
    );

    // Consultar comentários de determinado usuário
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long userId);

    // Consultar comentários de um usuário com paginação
    Page<Comment> findByAuthorIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    // Contar comentários de um documento
    long countByContentId(Long contentId);
}
