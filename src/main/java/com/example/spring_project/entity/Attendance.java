package com.example.spring_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
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
                @UniqueConstraint(columnNames = {"user_id", "attendanceDate"})
        }
)
public class Attendance extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @Column(nullable = false)
    LocalDate attendanceDate;

    @Builder.Default
    boolean attended = true;
}
