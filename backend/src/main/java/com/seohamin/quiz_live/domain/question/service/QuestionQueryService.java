package com.seohamin.quiz_live.domain.question.service;

import com.seohamin.quiz_live.domain.question.dto.StudentQuestionResponse;
import com.seohamin.quiz_live.domain.question.entity.Question;
import com.seohamin.quiz_live.domain.question.repository.QuestionRepository;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionQueryService {

    private final QuestionRepository questionRepository;

    public Question getQuestionForVideo(final Long videoId, final Long questionId) {
        return questionRepository.findByIdAndVideoId(questionId, videoId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND));
    }

    public StudentQuestionResponse getStudentQuestion(final Long videoId, final Long questionId) {
        return StudentQuestionResponse.from(getQuestionForVideo(videoId, questionId));
    }
}
