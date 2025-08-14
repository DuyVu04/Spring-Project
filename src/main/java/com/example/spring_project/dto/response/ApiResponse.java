package com.example.spring_project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import lombok.experimental.FieldDefaults;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ bao gồm các trường không null trả ra
public class ApiResponse<T> {
    @Builder.Default
    int code=1000;
    String message;
    T result;
}
