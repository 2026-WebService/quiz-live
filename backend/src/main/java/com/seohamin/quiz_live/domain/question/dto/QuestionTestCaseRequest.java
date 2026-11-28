package com.seohamin.quiz_live.domain.question.dto;

import com.seohamin.quiz_live.domain.question.entity.QuestionTestCase;
import jakarta.validation.constraints.NotNull;

public record QuestionTestCaseRequest(
        @NotNull String input,
        @NotNull String expectedOutput,
        boolean sample
) {
    public QuestionTestCase toEntity() {
        return new QuestionTestCase(input, expectedOutput, sample);
    }
}
