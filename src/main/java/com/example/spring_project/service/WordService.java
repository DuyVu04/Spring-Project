package com.example.spring_project.service;

import com.example.spring_project.dto.request.WordRequest;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.dto.response.WordResponse;
import com.example.spring_project.entity.Word;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.PageResponseMapper;
import com.example.spring_project.mapper.WordMapper;
import com.example.spring_project.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WordService {

    private final WordRepository wordRepository;
    private final WordMapper wordMapper;
    private final PageResponseMapper pageResponseMapper;

    /**
     * Search words in dictionary (for user search UI)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "words:search", key = "#keyword + ':' + #page + ':' + #size", unless = "#result == null")
    public PageResponse<WordResponse> searchWords(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Word> wordPage = wordRepository.findByWordContainingIgnoreCaseWithRelevance(keyword, pageable);
        return pageResponseMapper.toPageResponse(wordPage.map(wordMapper::toWordResponse));
    }

    /**
     * Get word of the day - deterministic random based on current date
     * Same word for all users on the same day
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "word:ofTheDay", key = "'today'", unless = "#result == null")
    public WordResponse getWordOfTheDay() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int seed = today.getYear() * 10000 + today.getMonthValue() * 100 + today.getDayOfMonth();
        long totalWords = wordRepository.count();
        if (totalWords == 0) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        java.util.Random random = new java.util.Random(seed);
        int randomIndex = random.nextInt((int) totalWords);
        Pageable pageable = PageRequest.of(randomIndex, 1);
        Page<Word> wordPage = wordRepository.findAll(pageable);

        if (wordPage.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return wordMapper.toWordResponse(wordPage.getContent().get(0));
    }

    /**
     * Get word detail by id
     */
    @Transactional(readOnly = true)
    public WordResponse getWordById(UUID id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return wordMapper.toWordResponse(word);
    }

    /**
     * Create a new word (admin or manual entry flow)
     */
    public WordResponse createWord(WordRequest request) {
        Word word = wordMapper.toWord(request);
        wordRepository.save(word);
        log.info("Created word: {}", word.getWord());
        return wordMapper.toWordResponse(word);
    }

    /**
     * Find or create word — used internally when user does manual entry
     */
    public Word findOrCreate(WordRequest request) {
        return wordRepository
                .findByWordIgnoreCaseAndPartOfSpeech(request.getWord(), request.getPartOfSpeech())
                .orElseGet(() -> {
                    Word newWord = wordMapper.toWord(request);
                    return wordRepository.save(newWord);
                });
    }
}
