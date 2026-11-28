package com.seohamin.quiz_live.domain.question;

import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.question.dto.QuestionCreateRequest;
import com.seohamin.quiz_live.domain.question.dto.QuestionResponse;
import com.seohamin.quiz_live.domain.question.dto.QuestionTestCaseRequest;
import com.seohamin.quiz_live.domain.question.dto.QuestionUpdateRequest;
import com.seohamin.quiz_live.domain.question.entity.Language;
import com.seohamin.quiz_live.domain.question.entity.QuestionType;
import com.seohamin.quiz_live.domain.question.repository.QuestionRepository;
import com.seohamin.quiz_live.domain.question.service.QuestionService;
import com.seohamin.quiz_live.domain.user.entity.Role;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.video.entity.Video;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import com.seohamin.quiz_live.support.TestFixtures;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestFixtures.class)
class QuestionServiceIntegrationTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestFixtures fixtures;

    private User professor;
    private Video video;

    @BeforeEach
    void setUp() {
        professor = fixtures.user(Role.PROFESSOR);
        Course course = fixtures.course(professor, "ABC234");
        video = fixtures.readyVideo(course);
    }

    @Test
    void 객관식_문제를_등록하고_목록과_단건으로_조회한다() {
        QuestionResponse created = questionService.createQuestion(professor.getId(), video.getId(), mcqRequest());

        assertThat(created.questionId()).isNotNull();
        assertThat(created.choices()).containsExactly("FIFO", "LIFO", "랜덤");
        assertThat(created.answerIndex()).isEqualTo(1);
        assertThat(questionService.getQuestions(professor.getId(), video.getId()))
                .extracting(QuestionResponse::questionId)
                .containsExactly(created.questionId());
        assertThat(questionService.getQuestion(professor.getId(), created.questionId()).title())
                .isEqualTo("스택의 특징");
    }

    @Test
    void 유형별_필수값이_잘못되면_400() {
        QuestionCreateRequest oneChoice = new QuestionCreateRequest(
                QuestionType.MCQ, "제목", "내용", 60, List.of("하나뿐"), 0, null, null, null);
        QuestionCreateRequest answerOutOfRange = new QuestionCreateRequest(
                QuestionType.MCQ, "제목", "내용", 60, List.of("A", "B"), 2, null, null, null);
        QuestionCreateRequest codeWithoutTestCase = new QuestionCreateRequest(
                QuestionType.CODE, "제목", "내용", 60, null, null, Set.of(Language.PYTHON), List.of(), null);

        for (QuestionCreateRequest request : List.of(oneChoice, answerOutOfRange, codeWithoutTestCase)) {
            assertThatThrownBy(() -> questionService.createQuestion(professor.getId(), video.getId(), request))
                    .isInstanceOf(CustomException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.INVALID_QUESTION_OPTION);
        }
    }

    @Test
    void 수업의_교수가_아니면_403() {
        User otherProfessor = fixtures.user(Role.PROFESSOR);
        QuestionResponse created = questionService.createQuestion(professor.getId(), video.getId(), mcqRequest());

        assertThatThrownBy(() -> questionService.createQuestion(otherProfessor.getId(), video.getId(), mcqRequest()))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.FORBIDDEN);
        assertThatThrownBy(() -> questionService.deleteQuestion(otherProfessor.getId(), created.questionId()))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.FORBIDDEN);
    }

    @Test
    void 코드_문제를_수정하면_언어와_테스트케이스가_교체된다() {
        QuestionResponse created = questionService.createQuestion(professor.getId(), video.getId(),
                new QuestionCreateRequest(QuestionType.CODE, "두 수의 합", "합을 출력", 300, null, null,
                        Set.of(Language.PYTHON),
                        List.of(new QuestionTestCaseRequest("1 2\n", "3\n", true)),
                        null));

        questionService.updateQuestion(professor.getId(), created.questionId(),
                new QuestionUpdateRequest("세 수의 합", "합을 출력", 600, null, null,
                        Set.of(Language.C, Language.JAVA),
                        List.of(
                                new QuestionTestCaseRequest("1 2 3\n", "6\n", true),
                                new QuestionTestCaseRequest("0 0 0\n", "0\n", false)
                        ),
                        null));
        entityManager.flush();
        entityManager.clear();

        QuestionResponse updated = questionService.getQuestion(professor.getId(), created.questionId());
        assertThat(updated.title()).isEqualTo("세 수의 합");
        assertThat(updated.timeLimitSec()).isEqualTo(600);
        assertThat(updated.languages()).containsExactlyInAnyOrder(Language.C, Language.JAVA);
        assertThat(updated.testCases()).extracting("input").containsExactly("1 2 3\n", "0 0 0\n");
    }

    @Test
    void 삭제한_문제는_다시_조회하면_404() {
        QuestionResponse created = questionService.createQuestion(professor.getId(), video.getId(), mcqRequest());

        questionService.deleteQuestion(professor.getId(), created.questionId());
        entityManager.flush();

        assertThat(questionRepository.findById(created.questionId())).isEmpty();
        assertThatThrownBy(() -> questionService.getQuestion(professor.getId(), created.questionId()))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.NOT_FOUND);
    }

    private QuestionCreateRequest mcqRequest() {
        return new QuestionCreateRequest(
                QuestionType.MCQ, "스택의 특징", "스택에 대한 설명으로 옳은 것은?", 60,
                List.of("FIFO", "LIFO", "랜덤"), 1, null, null, null);
    }
}
