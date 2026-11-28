package com.seohamin.quiz_live.domain.course.service;

import com.seohamin.quiz_live.domain.course.dto.CourseCreateRequest;
import com.seohamin.quiz_live.domain.course.dto.CourseJoinRequest;
import com.seohamin.quiz_live.domain.course.dto.CourseResponse;
import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.course.entity.CourseMember;
import com.seohamin.quiz_live.domain.course.repository.CourseMemberRepository;
import com.seohamin.quiz_live.domain.course.repository.CourseRepository;
import com.seohamin.quiz_live.domain.user.entity.Role;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.user.service.UserService;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    // 헷갈리기 쉬운 문자(0, O, 1, I)는 제외
    private static final String JOIN_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int JOIN_CODE_LENGTH = 6;
    private static final int MAX_JOIN_CODE_ATTEMPTS = 10;

    private final SecureRandom random = new SecureRandom();

    private final CourseRepository courseRepository;
    private final CourseMemberRepository courseMemberRepository;
    private final UserService userService;

    @Transactional
    public CourseResponse createCourse(final Long userId, final CourseCreateRequest request) {
        final User professor = userService.getUserEntity(userId);
        if (professor.getRole() != Role.PROFESSOR) {
            throw new CustomException(ExceptionCode.FORBIDDEN);
        }

        final Course course = courseRepository.save(new Course(
                request.title(),
                request.description(),
                professor,
                generateJoinCode()
        ));
        return CourseResponse.from(course);
    }

    @Transactional
    public CourseResponse joinCourse(final Long userId, final CourseJoinRequest request) {
        final User student = userService.getUserEntity(userId);
        if (student.getRole() != Role.STUDENT) {
            throw new CustomException(ExceptionCode.FORBIDDEN);
        }

        final Course course = courseRepository.findByJoinCode(request.joinCode().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND));

        if (courseMemberRepository.existsByCourseIdAndStudentId(course.getId(), student.getId())) {
            throw new CustomException(ExceptionCode.ALREADY_JOINED_COURSE);
        }
        try {
            // 동시에 두 번 참여 요청이 들어오면 유니크 제약(uk_course_member_course_student)에서 걸러진다
            courseMemberRepository.saveAndFlush(new CourseMember(course, student));
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ExceptionCode.ALREADY_JOINED_COURSE, e);
        }
        return CourseResponse.from(course);
    }

    private String generateJoinCode() {
        for (int attempt = 0; attempt < MAX_JOIN_CODE_ATTEMPTS; attempt++) {
            final StringBuilder code = new StringBuilder(JOIN_CODE_LENGTH);
            for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
                code.append(JOIN_CODE_CHARS.charAt(random.nextInt(JOIN_CODE_CHARS.length())));
            }
            if (!courseRepository.existsByJoinCode(code.toString())) {
                return code.toString();
            }
        }
        throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR);
    }
}
