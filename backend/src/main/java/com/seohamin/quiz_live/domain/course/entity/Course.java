package com.seohamin.quiz_live.domain.course.entity;

import com.seohamin.quiz_live.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "courses",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_join_code", columnNames = "join_code")
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @Column(name = "join_code", length = 6, nullable = false)
    private String joinCode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Course(
            final String title,
            final String description,
            final User professor,
            final String joinCode
    ) {
        this.title = title;
        this.description = description;
        this.professor = professor;
        this.joinCode = joinCode;
    }
}
