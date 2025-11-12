package com.example.spring_project.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title ;
    private String description ;
    private Integer durationInHours ;
    private String instructor ;
    private String level ;
    private String language ;
    private Boolean isActive ;
    private String thumbnailUrl ;
    private String videoUrl ;
    @ManyToMany
    Set<Category> categories = new HashSet<>();
}
