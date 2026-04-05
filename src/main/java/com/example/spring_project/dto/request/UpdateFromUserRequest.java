package com.example.spring_project.dto.request;

import com.example.spring_project.enums.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFromUserRequest {

    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String avatarImg;
    private String email;
}
