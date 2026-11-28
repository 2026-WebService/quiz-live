package com.seohamin.quiz_live.domain.question.dto;

import com.seohamin.quiz_live.domain.question.entity.Language;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

/**
 * 문제 유형(type)은 생성 후 바꿀 수 없으므로 받지 않는다. 기존 유형에 해당하는 필드만 사용한다.
 */
public record QuestionUpdateRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 4000) String content,
        @NotNull @Positive @Max(3600) Integer timeLimitSec,
        // MCQ
        List<@NotBlank @Size(max = 1000) String> choices,
        Integer answerIndex,
        // CODE
        Set<@NotNull Language> languages,
        List<@NotNull @Valid QuestionTestCaseRequest> testCases,
        // DRAWING
        Integer maxScore
) {
}
