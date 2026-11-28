package com.seohamin.quiz_live.domain.question.dto;

import com.seohamin.quiz_live.domain.question.entity.QuestionTestCase;

public record QuestionTestCaseResponse(
        String input,
        String expectedOutput,
        boolean sample
) {
    public static QuestionTestCaseResponse from(final QuestionTestCase testCase) {
        return new QuestionTestCaseResponse(testCase.getInput(), testCase.getExpectedOutput(), testCase.isSample());
    }
}
