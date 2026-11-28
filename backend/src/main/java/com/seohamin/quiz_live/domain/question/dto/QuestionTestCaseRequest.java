package com.seohamin.quiz_live.domain.question.dto;

import com.seohamin.quiz_live.domain.question.entity.QuestionTestCase;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionTestCaseRequest(
        @NotNull @Size(max = QuestionTestCase.MAX_TEXT_LENGTH) String input,
        @NotNull @Size(max = QuestionTestCase.MAX_TEXT_LENGTH) String expectedOutput,
        boolean sample
) {
    public QuestionTestCase toEntity() {
        return new QuestionTestCase(input, expectedOutput, sample);
    }
}
