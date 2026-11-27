package com.seohamin.quiz_live.domain.course.repository;

import com.seohamin.quiz_live.domain.course.entity.CourseMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseMemberRepository extends JpaRepository<CourseMember, Long> {

    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
}
