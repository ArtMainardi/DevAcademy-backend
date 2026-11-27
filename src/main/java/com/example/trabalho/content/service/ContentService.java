package com.example.trabalho.content.service;

import com.example.trabalho.content.dto.ContentResponseDTO;
import com.example.trabalho.content.model.Content;
import com.example.trabalho.content.model.ContentChange;
import com.example.trabalho.content.model.ContentHashtag;
import com.example.trabalho.content.repository.ContentChangeRepository;
import com.example.trabalho.content.repository.ContentHashtagRepository;
import com.example.trabalho.content.repository.ContentRepository;
import com.example.trabalho.hashtag.model.Hashtag;
import com.example.trabalho.hashtag.service.HashtagService;
import com.example.trabalho.user.model.User;
import com.example.trabalho.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentHashtagRepository contentHashtagRepository;
    private final ContentChangeRepository contentChangeRepository;
    private final UserRepository userRepository;
    private final HashtagService hashtagService;

    @Transactional
    public ContentResponseDTO createContent(
            String title,
            String body,
            Boolean published,
            List<String> rawHashtags,
            Long ownerId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário proprietário não encontrado."));

        Content content = Content.builder()
                .title(title)
                .body(body)
                .owner(owner)
                .published(published != null ? published : true)
                .build();

        Content savedContent = contentRepository.save(content);

        if (rawHashtags != null) {
            for (String tag : rawHashtags) {
                addHashtagToContent(savedContent.getId(), tag, ownerId);
            }
        }

        registerChange(savedContent, owner, "Criação inicial do documento");

        return ContentResponseDTO.fromEntity(savedContent);
    }

    @Transactional
    public ContentResponseDTO updateContent(
            Long contentId,
            String title,
            String body,
            String changeDescription,
            Long editorId,
            Long expectedVersion) {

        Content content = findContentEntityById(contentId);

        User editor = userRepository.findById(editorId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário editor não encontrado."));

        if (!canEdit(content, editorId)) {
            throw new AccessDeniedException(
                    "Apenas o proprietário ou colaboradores autorizados podem editar este conteúdo.");
        }

        if (expectedVersion != null && !expectedVersion.equals(content.getVersion())) {
            throw new OptimisticLockException(
                    "Conflito de edição detectado: o documento foi modificado por outro usuário enquanto você editava.");
        }

        if (title != null && !title.isBlank()) {
            content.setTitle(title);
        }
        if (body != null && !body.isBlank()) {
            content.setBody(body);
        }

        Content updated = contentRepository.save(content);
        String desc = (changeDescription != null && !changeDescription.isBlank())
                ? changeDescription
                : "Atualização de conteúdo";
        registerChange(updated, editor, desc);

        return ContentResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void deleteContent(Long contentId, Long requesterId) {
        Content content = findContentEntityById(contentId);
        if (!content.getOwner().getId().equals(requesterId)) {
            throw new AccessDeniedException("Apenas o proprietário original pode excluir este documento.");
        }
        contentRepository.delete(content);
    }

    @Transactional(readOnly = true)
    public ContentResponseDTO getContentById(Long contentId) {
        Content content = findContentEntityById(contentId);
        return ContentResponseDTO.fromEntity(content);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponseDTO> listUserOwnContents(Long ownerId, Pageable pageable) {
        return contentRepository.findByOwnerId(ownerId, pageable)
                .map(ContentResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponseDTO> explorePublishedContents(Pageable pageable) {
        return contentRepository.findByPublishedTrue(pageable)
                .map(ContentResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponseDTO> searchByKeyword(String keyword, Pageable pageable) {
        return contentRepository.searchByKeyword(keyword, pageable)
                .map(ContentResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponseDTO> searchByHashtags(List<String> hashtags, Pageable pageable) {
        List<String> normalized = hashtags.stream()
                .map(hashtagService::normalizeTag)
                .collect(Collectors.toList());

        return contentRepository.findByHashtags(normalized, pageable)
                .map(ContentResponseDTO::fromEntity);
    }

    @Transactional
    public void addHashtagToContent(Long contentId, String rawTag, Long requesterId) {
        Content content = findContentEntityById(contentId);
        if (!canEdit(content, requesterId)) {
            throw new AccessDeniedException("Sem permissão para alterar hashtags deste documento.");
        }
        Hashtag hashtag = hashtagService.findOrCreate(rawTag);
        if (!contentHashtagRepository.existsByContentIdAndHashtagId(contentId, hashtag.getId())) {
            ContentHashtag relation = ContentHashtag.builder()
                    .content(content)
                    .hashtag(hashtag)
                    .build();
            contentHashtagRepository.save(relation);
        }
    }

    @Transactional
    public void removeHashtagFromContent(Long contentId, Long hashtagId, Long requesterId) {
        Content content = findContentEntityById(contentId);
        if (!canEdit(content, requesterId)) {
            throw new AccessDeniedException("Sem permissão para remover hashtags deste documento.");
        }
        contentHashtagRepository.findByContentIdAndHashtagId(contentId, hashtagId)
                .ifPresent(contentHashtagRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<ContentChange> getChangeHistory(Long contentId) {
        return contentChangeRepository.findByContentIdOrderByChangedAtDesc(contentId);
    }

    public void registerChange(Content content, User user, String description) {
        ContentChange change = ContentChange.builder()
                .content(content)
                .changedBy(user)
                .description(description)
                .build();
        contentChangeRepository.save(change);
    }

    public boolean canEdit(Content content, Long userId) {
        return content.getOwner().getId().equals(userId);
    }

    private Content findContentEntityById(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado."));
    }
}