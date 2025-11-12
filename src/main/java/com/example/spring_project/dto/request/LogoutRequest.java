package com.example.spring_project.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class LogoutRequest {

    private String token;
}
