package com.example.spring_project.entity;

import com.example.spring_project.enums.PartOfSpeech;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "word", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"word", "part_of_speech"})
})
public class Word extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String word;

    private String pronunciation;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(name = "definition_vi", columnDefinition = "TEXT")
    private String definitionVi;

    @Column(columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(name = "example_sentence_vi", columnDefinition = "TEXT")
    private String exampleSentenceVi;

    // CEFR level: A1, A2, B1, B2, C1, C2
    private String level;
}
