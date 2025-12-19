package com.example.spring_project.entity;

import com.example.spring_project.listener.CustomAuditingEntityListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.ZonedDateTime;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(CustomAuditingEntityListener.class)
public class AbstractAuditEntity {

    @CreatedDate
    protected Instant createdAt;

    @LastModifiedDate
    protected Instant updatedAt;

    @LastModifiedBy
    protected String lastModifiedBy;

    @CreatedBy
    protected String createdBy;

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    protected Boolean isDeleted = false;
}
