package com.example.spring_project.mapper;

import com.example.spring_project.dto.response.VocabularyResponse;
import com.example.spring_project.entity.Vocabulary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WordMapper.class})
public interface VocabularyMapper {

    @Mapping(target = "word", source = "word")
    VocabularyResponse toVocabularyResponse(Vocabulary vocabulary);
}
