package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.WordRequest;
import com.example.spring_project.dto.response.WordResponse;
import com.example.spring_project.entity.Word;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WordMapper {

    Word toWord(WordRequest request);

    WordResponse toWordResponse(Word word);

    @Mapping(target = "id", ignore = true)
    void updateWordFromRequest(@MappingTarget Word word, WordRequest request);
}
