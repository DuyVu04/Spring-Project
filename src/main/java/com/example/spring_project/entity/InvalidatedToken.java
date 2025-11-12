package com.example.spring_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class InvalidatedToken {
    @Id
    private String id;
    private Date expiryTime;
}
