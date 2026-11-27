package com.seohamin.quiz_live.domain.question.dto;

import com.seohamin.quiz_live.domain.question.entity.QuestionTestCase;

public record SampleTestCaseResponse(
        String input,
        String expectedOutput
) {
    public static SampleTestCaseResponse from(final QuestionTestCase testCase) {
        return new SampleTestCaseResponse(testCase.getInput(), testCase.getExpectedOutput());
    }
}
