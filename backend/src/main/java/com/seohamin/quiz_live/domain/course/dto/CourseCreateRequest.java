package com.seohamin.quiz_live.domain.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseCreateRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 1000) String description
) {
}
