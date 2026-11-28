package com.seohamin.quiz_live.domain.question.service;

import com.seohamin.quiz_live.domain.course.service.CourseMembershipService;
import com.seohamin.quiz_live.domain.question.dto.QuestionCreateRequest;
import com.seohamin.quiz_live.domain.question.dto.QuestionResponse;
import com.seohamin.quiz_live.domain.question.dto.QuestionTestCaseRequest;
import com.seohamin.quiz_live.domain.question.dto.QuestionUpdateRequest;
import com.seohamin.quiz_live.domain.question.entity.Language;
import com.seohamin.quiz_live.domain.question.entity.Question;
import com.seohamin.quiz_live.domain.question.entity.QuestionTestCase;
import com.seohamin.quiz_live.domain.question.repository.QuestionRepository;
import com.seohamin.quiz_live.domain.video.entity.Video;
import com.seohamin.quiz_live.domain.video.service.VideoQueryService;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 교수용 문제 CRUD. 모든 작업은 문제가 속한 영상 → 수업의 교수만 할 수 있다.
 * TODO(14주차): 라이브 방에서 이미 출제된 문제는 수정·삭제를 막는다(409).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private static final int MIN_CHOICES = 2;
    private static final int MAX_CHOICES = 10;
    private static final int MAX_TEST_CASES = 50;

    private final QuestionRepository questionRepository;
    private final VideoQueryService videoQueryService;
    private final CourseMembershipService courseMembershipService;

    @Transactional
    public QuestionResponse createQuestion(
            final Long userId,
            final Long videoId,
            final QuestionCreateRequest request
    ) {
        final Video video = videoQueryService.getVideo(videoId);
        courseMembershipService.requireProfessor(video.getCourse().getId(), userId);

        final Question question = switch (request.type()) {
            case MCQ -> {
                validateMcq(request.choices(), request.answerIndex());
                yield Question.mcq(video, request.title(), request.content(), request.timeLimitSec(),
                        request.choices(), request.answerIndex());
            }
            case CODE -> {
                validateCode(request.languages(), request.testCases());
                yield Question.code(video, request.title(), request.content(), request.timeLimitSec(),
                        request.languages(), toTestCases(request.testCases()));
            }
            case DRAWING -> {
                validateDrawing(request.maxScore());
                yield Question.drawing(video, request.title(), request.content(), request.timeLimitSec(),
                        request.maxScore());
            }
        };

        return QuestionResponse.from(questionRepository.save(question));
    }

    public List<QuestionResponse> getQuestions(final Long userId, final Long videoId) {
        final Video video = videoQueryService.getVideo(videoId);
        courseMembershipService.requireProfessor(video.getCourse().getId(), userId);

        return questionRepository.findAllByVideoIdOrderByCreatedAtAsc(videoId).stream()
                .map(QuestionResponse::from)
                .toList();
    }

    public QuestionResponse getQuestion(final Long userId, final Long questionId) {
        return QuestionResponse.from(getOwnedQuestion(userId, questionId));
    }

    @Transactional
    public QuestionResponse updateQuestion(
            final Long userId,
            final Long questionId,
            final QuestionUpdateRequest request
    ) {
        final Question question = getOwnedQuestion(userId, questionId);

        switch (question.getType()) {
            case MCQ -> {
                validateMcq(request.choices(), request.answerIndex());
                question.updateMcq(request.choices(), request.answerIndex());
            }
            case CODE -> {
                validateCode(request.languages(), request.testCases());
                question.updateCode(request.languages(), toTestCases(request.testCases()));
            }
            case DRAWING -> {
                validateDrawing(request.maxScore());
                question.updateDrawing(request.maxScore());
            }
        }
        question.update(request.title(), request.content(), request.timeLimitSec());

        return QuestionResponse.from(question);
    }

    @Transactional
    public void deleteQuestion(final Long userId, final Long questionId) {
        questionRepository.delete(getOwnedQuestion(userId, questionId));
    }

    private Question getOwnedQuestion(final Long userId, final Long questionId) {
        final Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND));
        courseMembershipService.requireProfessor(question.getVideo().getCourse().getId(), userId);
        return question;
    }

    private void validateMcq(final List<String> choices, final Integer answerIndex) {
        if (choices == null || choices.size() < MIN_CHOICES || choices.size() > MAX_CHOICES) {
            throw new CustomException(ExceptionCode.INVALID_QUESTION_OPTION);
        }
        if (answerIndex == null || answerIndex < 0 || answerIndex >= choices.size()) {
            throw new CustomException(ExceptionCode.INVALID_QUESTION_OPTION);
        }
    }

    private void validateCode(final Set<Language> languages, final List<QuestionTestCaseRequest> testCases) {
        if (languages == null || languages.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_QUESTION_OPTION);
        }
        if (testCases == null || testCases.isEmpty() || testCases.size() > MAX_TEST_CASES) {
            throw new CustomException(ExceptionCode.INVALID_QUESTION_OPTION);
        }
    }

    private void validateDrawing(final Integer maxScore) {
        if (maxScore == null || maxScore <= 0) {
            throw new CustomException(ExceptionCode.INVALID_QUESTION_OPTION);
        }
    }

    private List<QuestionTestCase> toTestCases(final List<QuestionTestCaseRequest> requests) {
        return requests.stream().map(QuestionTestCaseRequest::toEntity).toList();
    }
}
