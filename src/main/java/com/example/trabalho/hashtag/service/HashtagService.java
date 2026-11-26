package com.example.trabalho.hashtag.service;

import com.example.trabalho.hashtag.model.Hashtag;
import com.example.trabalho.hashtag.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public String normalizeTag(String rawTag) {
        if (rawTag == null || rawTag.trim().isEmpty()) {
            throw new IllegalArgumentException("A hashtag não pode ser vazia.");
        }
        String normalized = Hashtag.normalize(rawTag);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Nome de hashtag inválido.");
        }
        return normalized;
    }

    @Transactional
    public Hashtag findOrCreate(String rawTag) {
        String normalized = normalizeTag(rawTag);
        return hashtagRepository.findByNormalizedName(normalized)
                .orElseGet(() -> {
                    String displayTag = rawTag.trim().startsWith("#") ? rawTag.trim() : "#" + rawTag.trim();
                    Hashtag newHashtag = Hashtag.builder()
                            .name(displayTag)
                            .normalizedName(normalized)
                            .build();
                    return hashtagRepository.save(newHashtag);
                });
    }

    @Transactional(readOnly = true)
    public List<Hashtag> searchByName(String query) {
        String normalized = Hashtag.normalize(query);
        return hashtagRepository.findByNormalizedNameContainingIgnoreCase(normalized);
    }

    @Transactional(readOnly = true)
    public List<Hashtag> suggestHashtags(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return hashtagRepository.findAllByOrderByNormalizedNameAsc();
        }
        String normalized = Hashtag.normalize(prefix);
        return hashtagRepository.findByNormalizedNameContainingIgnoreCase(normalized);
    }

    @Transactional(readOnly = true)
    public List<Hashtag> findAll() {
        return hashtagRepository.findAllByOrderByNormalizedNameAsc();
    }
}