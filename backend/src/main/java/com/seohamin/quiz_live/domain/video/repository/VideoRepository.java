package com.seohamin.quiz_live.domain.video.repository;

import com.seohamin.quiz_live.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findAllByCourseIdOrderByCreatedAtDesc(Long courseId);
}
