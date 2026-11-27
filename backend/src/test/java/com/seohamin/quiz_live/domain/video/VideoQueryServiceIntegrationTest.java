package com.seohamin.quiz_live.domain.video;

import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.course.repository.CourseRepository;
import com.seohamin.quiz_live.domain.user.dto.OauthUserRequestDto;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.user.repository.UserRepository;
import com.seohamin.quiz_live.domain.video.entity.Video;
import com.seohamin.quiz_live.domain.video.entity.VideoStatus;
import com.seohamin.quiz_live.domain.video.repository.VideoRepository;
import com.seohamin.quiz_live.domain.video.service.VideoQueryService;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VideoQueryServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoQueryService videoQueryService;

    @Test
    void 해당_수업의_READY_영상을_조회한다() {
        Course course = saveCourse("ABC123");
        Video video = videoRepository.save(new Video(
                course, "3주차 자료구조", "스택과 큐", VideoStatus.READY,
                "/api/v1/videos/7/hls/master.m3u8"
        ));

        Video found = videoQueryService.getReadyVideoForCourse(course.getId(), video.getId());

        assertThat(found.getId()).isEqualTo(video.getId());
    }

    @Test
    void 준비되지_않은_영상은_라이브에_사용할_수_없다() {
        Course course = saveCourse("ABC123");
        Video video = videoRepository.save(new Video(
                course, "변환 중 영상", null, VideoStatus.TRANSCODING, null
        ));

        assertExceptionCode(
                () -> videoQueryService.getReadyVideoForCourse(course.getId(), video.getId()),
                ExceptionCode.VIDEO_NOT_READY
        );
    }

    @Test
    void 다른_수업의_영상은_라이브에_사용할_수_없다() {
        Course requestedCourse = saveCourse("ABC123");
        Course otherCourse = saveCourse("XYZ789");
        Video video = videoRepository.save(new Video(
                otherCourse, "다른 수업 영상", null, VideoStatus.READY, "/hls/master.m3u8"
        ));

        assertExceptionCode(
                () -> videoQueryService.getReadyVideoForCourse(requestedCourse.getId(), video.getId()),
                ExceptionCode.FORBIDDEN
        );
    }

    @Test
    void 존재하지_않는_영상은_조회할_수_없다() {
        assertExceptionCode(() -> videoQueryService.getVideo(Long.MAX_VALUE), ExceptionCode.NOT_FOUND);
    }

    private Course saveCourse(final String joinCode) {
        User professor = userRepository.save(new User(new OauthUserRequestDto(
                "professor-" + joinCode,
                joinCode.toLowerCase() + "@example.com",
                "professor-" + joinCode,
                "김교수",
                null,
                null
        )));
        return courseRepository.save(new Course("웹서비스개발", null, professor, joinCode));
    }

    private void assertExceptionCode(final Runnable action, final ExceptionCode expected) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(expected);
    }
}
