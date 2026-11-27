package com.seohamin.quiz_live.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seohamin.quiz_live.domain.question.entity.Language;
import com.seohamin.quiz_live.domain.question.entity.Question;
import com.seohamin.quiz_live.domain.question.entity.QuestionType;

import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StudentQuestionResponse(
        Long questionId,
        QuestionType type,
        String title,
        String content,
        Integer timeLimitSec,
        List<String> choices,
        Set<Language> languages,
        List<SampleTestCaseResponse> sampleTestCases,
        Integer totalTestCaseCount,
        Integer maxScore
) {
    public static StudentQuestionResponse from(final Question question) {
        List<String> choices = question.getType() == QuestionType.MCQ
                ? List.copyOf(question.getChoices())
                : null;
        Set<Language> languages = question.getType() == QuestionType.CODE
                ? Set.copyOf(question.getLanguages())
                : null;
        List<SampleTestCaseResponse> samples = question.getType() == QuestionType.CODE
                ? question.getTestCases().stream()
                        .filter(testCase -> testCase.isSample())
                        .map(SampleTestCaseResponse::from)
                        .toList()
                : null;
        Integer totalCount = question.getType() == QuestionType.CODE
                ? question.getTestCases().size()
                : null;
        Integer maxScore = question.getType() == QuestionType.DRAWING
                ? question.getMaxScore()
                : null;

        return new StudentQuestionResponse(
                question.getId(),
                question.getType(),
                question.getTitle(),
                question.getContent(),
                question.getTimeLimitSec(),
                choices,
                languages,
                samples,
                totalCount,
                maxScore
        );
    }
}
