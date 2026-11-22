package com.example.trabalho.HashtagModule;

import com.seuprojeto.model.Hashtag;
import com.seuprojeto.repository.HashtagRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    // Normaliza o nome da hashtag
    public String normalizeHashtag(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da hashtag não pode ser vazio."
            );
        }

        String normalized = Normalizer
                .normalize(name.trim(), Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT);

        if (!normalized.startsWith("#")) {
            normalized = "#" + normalized;
        }

        return normalized;
    }

    // Cadastra uma nova hashtag ou retorna uma existente
    @Transactional
    public Hashtag findOrCreate(String name) {

        String normalizedName = normalizeHashtag(name);

        return hashtagRepository
                .findByNormalizedName(normalizedName)
                .orElseGet(() -> createHashtag(name, normalizedName));
    }

    // Cria uma hashtag
    private Hashtag createHashtag(String name, String normalizedName) {

        Hashtag hashtag = Hashtag.builder()
                .name(normalizedName)
                .normalizedName(normalizedName)
                .build();

        try {
            return hashtagRepository.save(hashtag);
        } catch (DataIntegrityViolationException exception) {
            // Trata possíveis cadastros simultâneos
            return hashtagRepository
                    .findByNormalizedName(normalizedName)
                    .orElseThrow(() -> exception);
        }
    }

    // Pesquisa uma hashtag pelo nome
    @Transactional(readOnly = true)
    public List<Hashtag> searchByName(String name) {

        String normalizedName = normalizeHashtag(name);

        return hashtagRepository
                .findByNormalizedNameContainingIgnoreCase(normalizedName);
    }

    // Sugere hashtags já cadastradas
    @Transactional(readOnly = true)
    public List<Hashtag> suggestHashtags(String term) {

        if (term == null || term.isBlank()) {
            return hashtagRepository.findAll();
        }

        String normalizedTerm = normalizeHashtag(term);

        return hashtagRepository
                .findByNormalizedNameContainingIgnoreCase(normalizedTerm);
    }

    // Verifica se uma hashtag já existe
    @Transactional(readOnly = true)
    public boolean exists(String name) {

        String normalizedName = normalizeHashtag(name);

        return hashtagRepository
                .existsByNormalizedName(normalizedName);
    }

    // Lista todas as hashtags cadastradas
    @Transactional(readOnly = true)
    public List<Hashtag> findAll() {
        return hashtagRepository.findAll();
    }
}
