package com.example.spring_project.entity;

import com.example.spring_project.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SuperBuilder
public class Course extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String title;

    String instructorName;

    String instructorAvatar;

    String instructorTitle;

    String description;

    String thumbnail;

    boolean free;

    Long totalDuration;

    Long totalModules;

    Long totalLessons;

    Double rating;

    Long totalEnrolled;

    @Enumerated(EnumType.STRING)
    CourseStatus status;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Module> modules = new ArrayList<>();

    // Best Practice: Hàm tiện ích để đồng bộ hai chiều khi thêm/xóa Module
    public void addModule(Module module) {
        modules.add(module);
        module.setCourse(this);
    }

    public void removeModule(Module module) {
        modules.remove(module);
        module.setCourse(null);
    }

}
