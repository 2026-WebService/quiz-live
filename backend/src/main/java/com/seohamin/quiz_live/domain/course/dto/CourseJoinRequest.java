package com.seohamin.quiz_live.domain.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseJoinRequest(
        @NotBlank @Size(min = 6, max = 6) String joinCode
) {
}
