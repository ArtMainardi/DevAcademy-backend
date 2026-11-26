package com.example.trabalho;
// Consultar comentários de um documento
@Transactional(readOnly = true)
public List<Comment> getComments(Long contentId) {

    if (!contentRepository.existsById(contentId)) {
        throw new EntityNotFoundException(
                "Documento não encontrado."
        );
    }

    return commentRepository
            .findByContentIdOrderByCreatedAtDesc(contentId);
}

// Consultar avaliações de um documento
@Transactional(readOnly = true)
public List<ContentRating> getRatings(Long contentId) {

    if (!contentRepository.existsById(contentId)) {
        throw new EntityNotFoundException(
                "Documento não encontrado."
        );
    }

    return contentRatingRepository
            .findByContentIdOrderByCreatedAtDesc(contentId);
}