package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.LessonNoteRequest;
import com.example.spring_project.dto.response.LessonNoteResponse;
import com.example.spring_project.entity.LessonNote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonNoteMapper {

    LessonNote toLessonNote(LessonNoteRequest request);

    LessonNoteResponse toLessonNoteResponse(LessonNote note);
}