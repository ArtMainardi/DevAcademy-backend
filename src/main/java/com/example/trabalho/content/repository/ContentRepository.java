package com.example.trabalho.content.repository;

import com.example.trabalho.content.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    Page<Content> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Content> findByPublishedTrue(Pageable pageable);

    Page<Content> findByPublishedTrueAndOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.published = true AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.body) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Content> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Content c JOIN c.hashtags ch JOIN ch.hashtag h " +
            "WHERE c.published = true AND h.normalizedName IN :normalizedHashtags")
    Page<Content> findByHashtags(@Param("normalizedHashtags") List<String> normalizedHashtags, Pageable pageable);

    Page<Content> findByPublishedTrueAndCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}