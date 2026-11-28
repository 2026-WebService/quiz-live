package com.seohamin.quiz_live.domain.course;

import com.seohamin.quiz_live.domain.course.dto.CourseCreateRequest;
import com.seohamin.quiz_live.domain.course.dto.CourseJoinRequest;
import com.seohamin.quiz_live.domain.course.dto.CourseResponse;
import com.seohamin.quiz_live.domain.course.repository.CourseMemberRepository;
import com.seohamin.quiz_live.domain.course.service.CourseService;
import com.seohamin.quiz_live.domain.user.entity.Role;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import com.seohamin.quiz_live.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestFixtures.class)
class CourseServiceIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseMemberRepository courseMemberRepository;

    @Autowired
    private TestFixtures fixtures;

    @Test
    void 교수가_수업을_만들면_6자리_참여_코드가_발급된다() {
        User professor = fixtures.user(Role.PROFESSOR);

        CourseResponse response = courseService.createCourse(
                professor.getId(), new CourseCreateRequest("웹서비스개발", "13주차"));

        assertThat(response.courseId()).isNotNull();
        assertThat(response.professorId()).isEqualTo(professor.getId());
        assertThat(response.joinCode()).matches("[A-Z2-9]{6}");
    }

    @Test
    void 교수가_아니면_수업을_만들_수_없다() {
        User student = fixtures.user(Role.STUDENT);

        assertThatThrownBy(() -> courseService.createCourse(student.getId(), new CourseCreateRequest("수업", null)))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.FORBIDDEN);
    }

    @Test
    void 학생은_참여_코드로_수업에_참여하고_중복_참여는_409() {
        User professor = fixtures.user(Role.PROFESSOR);
        User student = fixtures.user(Role.STUDENT);
        fixtures.course(professor, "ABC234");

        CourseResponse joined = courseService.joinCourse(student.getId(), new CourseJoinRequest("abc234"));

        assertThat(courseMemberRepository.existsByCourseIdAndStudentId(joined.courseId(), student.getId())).isTrue();
        assertThatThrownBy(() -> courseService.joinCourse(student.getId(), new CourseJoinRequest("ABC234")))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.ALREADY_JOINED_COURSE);
    }

    @Test
    void 없는_참여_코드는_404() {
        User student = fixtures.user(Role.STUDENT);

        assertThatThrownBy(() -> courseService.joinCourse(student.getId(), new CourseJoinRequest("ZZZZZZ")))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.NOT_FOUND);
    }
}
