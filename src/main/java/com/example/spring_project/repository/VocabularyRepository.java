package com.example.spring_project.repository;

import com.example.spring_project.entity.Vocabulary;
import com.example.spring_project.enums.WordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID>, JpaSpecificationExecutor<Vocabulary> {

    Page<Vocabulary> findByUserId(Long userId, Pageable pageable);

    Page<Vocabulary> findByUserIdAndStatus(Long userId, WordStatus status, Pageable pageable);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, WordStatus status);

    boolean existsByUserIdAndWordId(Long userId, UUID wordId);
}
