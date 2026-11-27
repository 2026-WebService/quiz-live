package com.seohamin.quiz_live.domain.course.service;

import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.course.repository.CourseMemberRepository;
import com.seohamin.quiz_live.domain.course.repository.CourseRepository;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseMembershipService {

    private final CourseRepository courseRepository;
    private final CourseMemberRepository courseMemberRepository;

    public Course getCourse(final Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND));
    }

    public boolean isProfessor(final Long courseId, final Long userId) {
        Course course = getCourse(courseId);
        return course.getProfessor().getId().equals(userId);
    }

    public boolean isStudent(final Long courseId, final Long userId) {
        getCourse(courseId);
        return courseMemberRepository.existsByCourseIdAndStudentId(courseId, userId);
    }

    public boolean isMember(final Long courseId, final Long userId) {
        return isProfessor(courseId, userId) || isStudent(courseId, userId);
    }

    public Course requireProfessor(final Long courseId, final Long userId) {
        Course course = getCourse(courseId);
        if (!course.getProfessor().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN);
        }
        return course;
    }

    public Course requireMember(final Long courseId, final Long userId) {
        Course course = getCourse(courseId);
        boolean professor = course.getProfessor().getId().equals(userId);
        boolean student = courseMemberRepository.existsByCourseIdAndStudentId(courseId, userId);
        if (!professor && !student) {
            throw new CustomException(ExceptionCode.FORBIDDEN);
        }
        return course;
    }
}
