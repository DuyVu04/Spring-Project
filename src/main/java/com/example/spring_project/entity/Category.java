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
public class Category {
    @Id
    private String name;
    private String description;

}
