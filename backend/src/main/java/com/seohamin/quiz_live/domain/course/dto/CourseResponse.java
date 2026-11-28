package com.seohamin.quiz_live.domain.course.dto;

import com.seohamin.quiz_live.domain.course.entity.Course;

import java.time.Instant;

public record CourseResponse(
        Long courseId,
        String title,
        String description,
        Long professorId,
        String professorName,
        String joinCode,
        Instant createdAt
) {
    public static CourseResponse from(final Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getProfessor().getId(),
                course.getProfessor().getName(),
                course.getJoinCode(),
                course.getCreatedAt()
        );
    }
}
