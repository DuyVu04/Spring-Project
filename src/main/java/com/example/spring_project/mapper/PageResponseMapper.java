package com.example.spring_project.mapper;

import com.example.spring_project.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;


public interface PageResponseMapper {
    <T> PageResponse<T> toPageResponse(Page<T> page);

    default List<PageResponse.Sort> toSort(Sort sort) {
        return sort.stream()
                .map(o -> new PageResponse.Sort(o.getDirection().name(), o.getProperty()))
                .collect(Collectors.toList());
    }
}
