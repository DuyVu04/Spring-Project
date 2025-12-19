package com.example.spring_project.repository;

import com.example.spring_project.entity.Word;
import com.example.spring_project.enums.PartOfSpeech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WordRepository extends JpaRepository<Word, UUID> {

    Optional<Word> findByWordIgnoreCase(String word);

    Optional<Word> findByWordIgnoreCaseAndPartOfSpeech(String word, PartOfSpeech partOfSpeech);

    Page<Word> findByWordContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * Search words with relevance-based sorting.
     * Results are ordered by:
     * 1. Exact matches (LOWER(word) = LOWER(keyword))
     * 2. Starts-with matches (LOWER(word) LIKE LOWER(keyword%))
     * 3. Contains matches (LOWER(word) LIKE LOWER(%keyword%))
     * Within each category, results are sorted alphabetically.
     */
    @Query("SELECT w FROM Word w " +
            "WHERE LOWER(w.word) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY " +
            "CASE " +
            "  WHEN LOWER(w.word) = LOWER(:keyword) THEN 1 " +
            "  WHEN LOWER(w.word) LIKE LOWER(CONCAT(:keyword, '%')) THEN 2 " +
            "  ELSE 3 " +
            "END, " +
            "w.word ASC")
    Page<Word> findByWordContainingIgnoreCaseWithRelevance(@Param("keyword") String keyword, Pageable pageable);

    List<Word> findByLevel(String level);
}
