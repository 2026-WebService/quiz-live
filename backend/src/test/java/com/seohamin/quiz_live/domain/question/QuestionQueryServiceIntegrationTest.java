package com.seohamin.quiz_live.domain.question;

import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.course.repository.CourseRepository;
import com.seohamin.quiz_live.domain.question.dto.StudentQuestionResponse;
import com.seohamin.quiz_live.domain.question.entity.Language;
import com.seohamin.quiz_live.domain.question.entity.Question;
import com.seohamin.quiz_live.domain.question.entity.QuestionTestCase;
import com.seohamin.quiz_live.domain.question.repository.QuestionRepository;
import com.seohamin.quiz_live.domain.question.service.QuestionQueryService;
import com.seohamin.quiz_live.domain.user.dto.OauthUserRequestDto;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.user.repository.UserRepository;
import com.seohamin.quiz_live.domain.video.entity.Video;
import com.seohamin.quiz_live.domain.video.entity.VideoStatus;
import com.seohamin.quiz_live.domain.video.repository.VideoRepository;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuestionQueryServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionQueryService questionQueryService;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void 코드_문제의_학생용_응답에는_샘플_테스트케이스만_포함한다() {
        Video video = saveVideo("ABC123");
        Question question = questionRepository.save(Question.code(
                video,
                "두 수의 합",
                "두 정수를 입력받아 합을 출력하세요.",
                300,
                Set.of(Language.PYTHON, Language.C, Language.JAVA),
                List.of(
                        new QuestionTestCase("1 2\n", "3\n", true),
                        new QuestionTestCase("-5 10\n", "5\n", false)
                )
        ));

        StudentQuestionResponse response = questionQueryService.getStudentQuestion(video.getId(), question.getId());

        assertThat(response.languages()).containsExactlyInAnyOrder(Language.PYTHON, Language.C, Language.JAVA);
        assertThat(response.sampleTestCases()).hasSize(1);
        assertThat(response.sampleTestCases().getFirst().input()).isEqualTo("1 2\n");
        assertThat(response.sampleTestCases().getFirst().expectedOutput()).isEqualTo("3\n");
        assertThat(response.totalTestCaseCount()).isEqualTo(2);
    }

    @Test
    void 객관식_학생용_응답에는_보기만_포함한다() throws Exception {
        Video video = saveVideo("ABC123");
        Question question = questionRepository.save(Question.mcq(
                video,
                "스택의 특징",
                "스택에 대한 설명으로 옳은 것은?",
                60,
                List.of("FIFO 구조", "임의 접근 가능", "LIFO 구조", "정렬 상태 유지"),
                2
        ));

        StudentQuestionResponse response = questionQueryService.getStudentQuestion(video.getId(), question.getId());

        assertThat(response.choices()).containsExactly("FIFO 구조", "임의 접근 가능", "LIFO 구조", "정렬 상태 유지");
        assertThat(response.languages()).isNull();
        assertThat(response.sampleTestCases()).isNull();
        String json = jsonMapper.writeValueAsString(response);
        assertThat(json).doesNotContain("languages", "sampleTestCases", "totalTestCaseCount", "maxScore");
    }

    @Test
    void 다른_영상의_문제는_조회하지_못한다() {
        Video requestedVideo = saveVideo("ABC123");
        Video otherVideo = saveVideo("XYZ789");
        Question question = questionRepository.save(Question.drawing(
                otherVideo, "이진 트리", "이진 트리를 그리세요.", 120, 10
        ));

        assertThatThrownBy(() -> questionQueryService.getQuestionForVideo(requestedVideo.getId(), question.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.NOT_FOUND);
    }

    private Video saveVideo(final String joinCode) {
        User professor = userRepository.save(new User(new OauthUserRequestDto(
                "professor-" + joinCode,
                joinCode.toLowerCase() + "@example.com",
                "professor-" + joinCode,
                "김교수",
                null,
                null
        )));
        Course course = courseRepository.save(new Course("웹서비스개발", null, professor, joinCode));
        return videoRepository.save(new Video(course, "3주차 자료구조", null, VideoStatus.READY, "/hls/master.m3u8"));
    }
}
