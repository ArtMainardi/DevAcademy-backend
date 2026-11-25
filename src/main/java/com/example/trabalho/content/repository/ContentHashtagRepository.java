package com.example.trabalho.content.repository;

import com.example.trabalho.content.model.ContentHashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentHashtagRepository extends JpaRepository<ContentHashtag, Long> {

    List<ContentHashtag> findByContentId(Long contentId);

    Page<ContentHashtag> findByHashtagNormalizedName(String normalizedName, Pageable pageable);

    boolean existsByContentIdAndHashtagId(Long contentId, Long hashtagId);

    Optional<ContentHashtag> findByContentIdAndHashtagId(Long contentId, Long hashtagId);
}