package com.example.spring_project.service;

import com.example.spring_project.dto.request.VocabularyFilterRequest;
import com.example.spring_project.dto.request.VocabularyRequest;
import com.example.spring_project.dto.request.WordRequest;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.dto.response.VocabularyResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.entity.Vocabulary;
import com.example.spring_project.entity.Word;
import com.example.spring_project.enums.WordStatus;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.PageResponseMapper;
import com.example.spring_project.mapper.VocabularyMapper;
import com.example.spring_project.repository.UserRepository;
import com.example.spring_project.repository.VocabularyRepository;
import com.example.spring_project.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final VocabularyMapper vocabularyMapper;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final WordService wordService;
    private final PageResponseMapper pageResponseMapper;

    /**
     * Add word to user's notebook.
     * Two flows:
     *   1. From dictionary: request.wordId is provided
     *   2. Manual entry: request.wordId is null, word fields are filled
     */
    public VocabularyResponse addToNotebook(VocabularyRequest request) {
        User currentUser = getCurrentUser();

        Word word;

        if (request.getWordId() != null) {
            word = wordRepository.findById(request.getWordId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        } else {
            if (request.getWord() == null || request.getWord().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT);
            }
            WordRequest wordRequest = WordRequest.builder()
                    .word(request.getWord())
                    .pronunciation(request.getPronunciation())
                    .partOfSpeech(request.getPartOfSpeech())
                    .definition(request.getDefinition())
                    .exampleSentence(request.getExampleSentence())
                    .level(request.getLevel())
                    .build();
            word = wordService.findOrCreate(wordRequest);
        }

        if (vocabularyRepository.existsByUserIdAndWordId(currentUser.getId(), word.getId())) {
            throw new CustomException(ErrorCode.RESOURCE_EXISTS);
        }

        Vocabulary vocabulary = Vocabulary.builder()
                .user(currentUser)
                .word(word)
                .status(WordStatus.TO_LEARN)
                .favorite(false)
                .note(request.getNote())
                .build();

        vocabularyRepository.save(vocabulary);

        log.info("Added word '{}' to notebook for user {}", word.getWord(), currentUser.getUsername());

        return vocabularyMapper.toVocabularyResponse(vocabulary);
    }

    /**
     * Get user's notebook with pagination and filter
     */
    @Transactional(readOnly = true)
    public PageResponse<VocabularyResponse> getAllVocabularies(
            VocabularyFilterRequest filterRequest,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        User currentUser = getCurrentUser();

        Specification<Vocabulary> spec = buildSpecification(filterRequest, currentUser.getId());

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Vocabulary> vocabularyPage = vocabularyRepository.findAll(spec, pageable);

        return pageResponseMapper.toPageResponse(
                vocabularyPage.map(vocabularyMapper::toVocabularyResponse)
        );
    }

    /**
     * Get single vocabulary entry
     */
    @Transactional(readOnly = true)
    public VocabularyResponse getVocabulary(UUID vocabularyId) {
        User currentUser = getCurrentUser();
        Vocabulary vocabulary = getUserVocabulary(vocabularyId, currentUser);
        return vocabularyMapper.toVocabularyResponse(vocabulary);
    }

    /**
     * Update notebook entry (note, status, favorite)
     */
    public VocabularyResponse updateVocabulary(UUID vocabularyId, VocabularyRequest request) {
        User currentUser = getCurrentUser();
        Vocabulary vocabulary = getUserVocabulary(vocabularyId, currentUser);

        if (request.getNote() != null) {
            vocabulary.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            vocabulary.setStatus(request.getStatus());
        }
        if (request.getFavorite() != null) {
            vocabulary.setFavorite(request.getFavorite());
        }

        vocabularyRepository.save(vocabulary);

        log.info("Updated vocabulary {} for user {}", vocabularyId, currentUser.getUsername());

        return vocabularyMapper.toVocabularyResponse(vocabulary);
    }

    /**
     * Remove word from notebook
     */
    public void deleteVocabulary(UUID vocabularyId) {
        User currentUser = getCurrentUser();
        Vocabulary vocabulary = getUserVocabulary(vocabularyId, currentUser);
        vocabularyRepository.delete(vocabulary);
        log.info("Removed vocabulary {} from notebook for user {}", vocabularyId, currentUser.getUsername());
    }

    /**
     * Toggle favorite
     */
    public VocabularyResponse toggleFavorite(UUID vocabularyId) {
        User currentUser = getCurrentUser();
        Vocabulary vocabulary = getUserVocabulary(vocabularyId, currentUser);
        vocabulary.setFavorite(!Boolean.TRUE.equals(vocabulary.getFavorite()));
        vocabularyRepository.save(vocabulary);
        return vocabularyMapper.toVocabularyResponse(vocabulary);
    }

    /**
     * Mark as mastered
     */
    public VocabularyResponse markAsMastered(UUID vocabularyId) {
        User currentUser = getCurrentUser();
        Vocabulary vocabulary = getUserVocabulary(vocabularyId, currentUser);
        vocabulary.setStatus(WordStatus.MASTERED);
        vocabularyRepository.save(vocabulary);
        log.info("Marked vocabulary {} as MASTERED for user {}", vocabularyId, currentUser.getUsername());
        return vocabularyMapper.toVocabularyResponse(vocabulary);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
    }

    private Vocabulary getUserVocabulary(UUID vocabularyId, User user) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!vocabulary.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        return vocabulary;
    }

    private Specification<Vocabulary> buildSpecification(VocabularyFilterRequest filterRequest, Long userId) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (filterRequest.getKeyword() != null && !filterRequest.getKeyword().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("word").get("word")),
                        "%" + filterRequest.getKeyword().toLowerCase() + "%"
                ));
            }

            if (filterRequest.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filterRequest.getStatus()));
            }

            if (filterRequest.getFavorite() != null) {
                predicates.add(cb.equal(root.get("favorite"), filterRequest.getFavorite()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
