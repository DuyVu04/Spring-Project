package com.example.spring_project.dto.response;

import com.example.spring_project.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
     Long id;
     String username;
     String firstName;
     String lastName;
     LocalDate dob;
     Set<RoleResponse>roles;
}
