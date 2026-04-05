package com.example.spring_project.controller;

import com.example.spring_project.dto.request.WordRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.dto.response.WordResponse;
import com.example.spring_project.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    /**
     * Search dictionary — used by user search UI
     * GET /words/search?keyword=run&page=0&size=20
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<WordResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.<PageResponse<WordResponse>>builder()
                .result(wordService.searchWords(keyword, page, size))
                .build();
    }

    /**
     * Get word detail
     * GET /words/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<WordResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<WordResponse>builder()
                .result(wordService.getWordById(id))
                .build();
    }

    /**
     * Create word (admin / crawler import)
     * POST /words
     */
    @PostMapping
    public ApiResponse<WordResponse> createWord(@RequestBody WordRequest request) {
        return ApiResponse.<WordResponse>builder()
                .result(wordService.createWord(request))
                .build();
    }

    /**
     * Get word of the day - deterministic random based on current date
     * GET /words/word-of-day
     */
    @GetMapping("/word-of-day")
    public ApiResponse<WordResponse> getWordOfTheDay() {
        return ApiResponse.<WordResponse>builder()
                .result(wordService.getWordOfTheDay())
                .build();
    }
}
