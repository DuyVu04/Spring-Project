package com.example.spring_project.dto.request;

import com.example.spring_project.enums.MembershipType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserUpdateRequest {
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private MembershipType membershipType;
    private String avatarImg;
    private String email;
    private List<String> roles;

}