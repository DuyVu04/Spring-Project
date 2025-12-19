package com.example.spring_project.service;

import com.example.spring_project.dto.request.LessonNoteRequest;
import com.example.spring_project.dto.response.LessonNoteResponse;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.LessonNoteMapper;
import com.example.spring_project.repository.LessonNoteRepository;
import com.example.spring_project.repository.LessonRepository;
import com.example.spring_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonNoteService {

    private final LessonNoteRepository lessonNoteRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LessonNoteMapper lessonNoteMapper;

    public LessonNoteResponse createNote(LessonNoteRequest request) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        var lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        var note = lessonNoteMapper.toLessonNote(request);
        note.setUser(user);
        note.setLesson(lesson);
        lessonNoteRepository.save(note);

        return lessonNoteMapper.toLessonNoteResponse(note);
    }

    public List<LessonNoteResponse> getMyNotesByLesson(UUID lessonId) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return lessonNoteRepository.findByUserIdAndLessonId(user.getId(), lessonId).stream()
                .map(lessonNoteMapper::toLessonNoteResponse)
                .collect(Collectors.toList());
    }

    public LessonNoteResponse updateNote(UUID noteId, LessonNoteRequest request) {
        var note = lessonNoteRepository.findById(noteId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        note.setContent(request.getContent());
        note.setVideoTimestamp(request.getVideoTimestamp());
        lessonNoteRepository.save(note);

        return lessonNoteMapper.toLessonNoteResponse(note);
    }

    public void deleteNote(UUID noteId) {
        if (!lessonNoteRepository.existsById(noteId)) {
            throw new CustomException(ErrorCode.LESSON_NOT_FOUND);
        }
        lessonNoteRepository.deleteById(noteId);
    }
}
