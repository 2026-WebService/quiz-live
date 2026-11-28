package com.seohamin.quiz_live.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seohamin.quiz_live.domain.question.entity.Language;
import com.seohamin.quiz_live.domain.question.entity.Question;
import com.seohamin.quiz_live.domain.question.entity.QuestionType;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * 교수용 문제 응답. 정답(answerIndex)과 전체 테스트케이스를 포함하므로 학생에게는 StudentQuestionResponse를 쓴다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record QuestionResponse(
        Long questionId,
        Long videoId,
        QuestionType type,
        String title,
        String content,
        Integer timeLimitSec,
        List<String> choices,
        Integer answerIndex,
        Set<Language> languages,
        List<QuestionTestCaseResponse> testCases,
        Integer maxScore,
        Instant createdAt
) {
    public static QuestionResponse from(final Question question) {
        final boolean mcq = question.getType() == QuestionType.MCQ;
        final boolean code = question.getType() == QuestionType.CODE;
        final boolean drawing = question.getType() == QuestionType.DRAWING;

        return new QuestionResponse(
                question.getId(),
                question.getVideo().getId(),
                question.getType(),
                question.getTitle(),
                question.getContent(),
                question.getTimeLimitSec(),
                mcq ? List.copyOf(question.getChoices()) : null,
                mcq ? question.getAnswerIndex() : null,
                code ? Set.copyOf(question.getLanguages()) : null,
                code ? question.getTestCases().stream().map(QuestionTestCaseResponse::from).toList() : null,
                drawing ? question.getMaxScore() : null,
                question.getCreatedAt()
        );
    }
}
