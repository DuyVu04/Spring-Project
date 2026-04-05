package com.example.spring_project.entity;

import com.example.spring_project.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SuperBuilder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id","course_id"})
        }
)
public class Enrollment extends AbstractAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    Course course;

    Integer completionPercent;

    @Enumerated(EnumType.STRING)
    EnrollmentStatus status;
}
