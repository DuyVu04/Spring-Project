package com.example.spring_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "lesson_quiz_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonQuizQuestion extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    String questionText;

    @Column(name = "option_a", nullable = false, columnDefinition = "TEXT")
    String optionA;

    @Column(name = "option_b", nullable = false, columnDefinition = "TEXT")
    String optionB;

    @Column(name = "option_c", nullable = false, columnDefinition = "TEXT")
    String optionC;

    @Column(name = "option_d", nullable = false, columnDefinition = "TEXT")
    String optionD;

    @Column(name = "correct_answer", nullable = false, length = 1)
    String correctAnswer;

    @Column(columnDefinition = "TEXT")
    String explanation;

    @Column(name = "display_order", nullable = false)
    Integer displayOrder;
}