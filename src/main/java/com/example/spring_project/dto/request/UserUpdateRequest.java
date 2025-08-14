package com.example.spring_project.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE) // Tất cả các field dữ liệu sẽ có kiểu là private
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    LocalDate dob;


}