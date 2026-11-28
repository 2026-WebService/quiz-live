package com.seohamin.quiz_live.domain.question.dto;

import com.seohamin.quiz_live.domain.question.entity.Language;
import com.seohamin.quiz_live.domain.question.entity.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

/**
 * 공통 필드는 Bean Validation으로, 유형별 필수값(보기·언어·테스트케이스·배점)은 서비스에서 검증한다.
 */
public record QuestionCreateRequest(
        @NotNull QuestionType type,
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
