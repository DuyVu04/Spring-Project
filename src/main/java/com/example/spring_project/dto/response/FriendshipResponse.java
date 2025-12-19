package com.example.spring_project.dto.response;

import com.example.spring_project.enums.FriendshipStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipResponse {
    Long id;
    UserResponse requester;
    UserResponse addressee;
    FriendshipStatus status;
    Instant createdAt;
}
