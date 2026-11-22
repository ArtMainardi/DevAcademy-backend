package com.example.trabalho.HashtagModule;

import com.seuprojeto.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    // Buscar hashtag pelo nome normalizado
    Optional<Hashtag> findByNormalizedName(String normalizedName);

    // Verificar se uma hashtag já existe
    boolean existsByNormalizedName(String normalizedName);

    // Pesquisar hashtags por parte do nome normalizado
    List<Hashtag> findByNormalizedNameContainingIgnoreCase(String name);

    // Listar todas as hashtags existentes
    @Override
    List<Hashtag> findAll();
}
