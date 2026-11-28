package com.seohamin.quiz_live.domain.course.repository;

import com.seohamin.quiz_live.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByJoinCode(String joinCode);

    boolean existsByJoinCode(String joinCode);
}
