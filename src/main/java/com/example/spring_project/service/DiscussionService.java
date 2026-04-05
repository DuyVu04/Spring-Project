package com.example.spring_project.service;

import com.example.spring_project.dto.request.DiscussionRequest;
import com.example.spring_project.dto.response.DiscussionResponse;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.DiscussionMapper;
import com.example.spring_project.repository.DiscussionRepository;
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
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final DiscussionMapper discussionMapper;

    public DiscussionResponse createDiscussion(DiscussionRequest request) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        var lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        var discussion = discussionMapper.toDiscussion(request);
        discussion.setUser(user);
        discussion.setLesson(lesson);


        if (request.getParentDiscussionId() != null) {
            var parent = discussionRepository.findById(request.getParentDiscussionId())
                    .orElseThrow(() -> new CustomException(ErrorCode.DISCUSSION_NOT_FOUND));
            discussion.setParentDiscussion(parent);
        }

        discussionRepository.save(discussion);
        return discussionMapper.toDiscussionResponse(discussion);
    }

    public List<DiscussionResponse> getDiscussionsByLesson(UUID lessonId) {
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));


        return discussionRepository.findByLessonIdAndParentDiscussionIsNull(lessonId).stream()
                .map(discussionMapper::toDiscussionResponse)
                .collect(Collectors.toList());
    }

    public void deleteDiscussion(UUID discussionId) {
        var discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new CustomException(ErrorCode.DISCUSSION_NOT_FOUND));
        discussionRepository.delete(discussion);
    }
}
