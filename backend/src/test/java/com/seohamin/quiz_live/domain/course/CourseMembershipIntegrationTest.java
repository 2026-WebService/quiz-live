package com.seohamin.quiz_live.domain.course;

import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.course.entity.CourseMember;
import com.seohamin.quiz_live.domain.course.repository.CourseMemberRepository;
import com.seohamin.quiz_live.domain.course.repository.CourseRepository;
import com.seohamin.quiz_live.domain.course.service.CourseMembershipService;
import com.seohamin.quiz_live.domain.user.dto.OauthUserRequestDto;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.user.repository.UserRepository;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseMembershipIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMemberRepository courseMemberRepository;

    @Autowired
    private CourseMembershipService courseMembershipService;

    @Test
    void 교수와_학생의_수업_멤버십을_판별한다() {
        User professor = saveUser("professor-sub", "professor@example.com");
        User student = saveUser("student-sub", "student@example.com");
        User outsider = saveUser("outsider-sub", "outsider@example.com");
        Course course = courseRepository.save(new Course("웹서비스개발", "2026-2", professor, "ABC123"));
        courseMemberRepository.save(new CourseMember(course, student));

        assertThat(courseMembershipService.isProfessor(course.getId(), professor.getId())).isTrue();
        assertThat(courseMembershipService.isStudent(course.getId(), student.getId())).isTrue();
        assertThat(courseMembershipService.isMember(course.getId(), professor.getId())).isTrue();
        assertThat(courseMembershipService.isMember(course.getId(), student.getId())).isTrue();
        assertThat(courseMembershipService.isMember(course.getId(), outsider.getId())).isFalse();
    }

    @Test
    void 멤버가_아닌_사용자의_접근을_거부한다() {
        User professor = saveUser("professor-sub", "professor@example.com");
        User outsider = saveUser("outsider-sub", "outsider@example.com");
        Course course = courseRepository.save(new Course("웹서비스개발", null, professor, "ABC123"));

        assertThatThrownBy(() -> courseMembershipService.requireMember(course.getId(), outsider.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.FORBIDDEN);
    }

    @Test
    void 같은_학생은_같은_수업에_두_번_참여할_수_없다() {
        User professor = saveUser("professor-sub", "professor@example.com");
        User student = saveUser("student-sub", "student@example.com");
        Course course = courseRepository.save(new Course("웹서비스개발", null, professor, "ABC123"));
        courseMemberRepository.saveAndFlush(new CourseMember(course, student));

        assertThatThrownBy(() -> courseMemberRepository.saveAndFlush(new CourseMember(course, student)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private User saveUser(final String providerUserId, final String email) {
        return userRepository.save(new User(new OauthUserRequestDto(
                providerUserId,
                email,
                providerUserId,
                providerUserId,
                null,
                null
        )));
    }
}
