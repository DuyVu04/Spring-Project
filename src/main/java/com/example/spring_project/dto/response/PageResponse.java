package com.example.spring_project.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    @Builder.Default private List<T> content = new ArrayList<>();
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    @Builder.Default private List<Sort> sorts = new ArrayList<>();

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Sort {
        private String direction;
        private String property;
    }
}
