package com.seohamin.quiz_live.domain.question.repository;

import com.seohamin.quiz_live.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findByIdAndVideoId(Long questionId, Long videoId);

    List<Question> findAllByVideoIdOrderByCreatedAtAsc(Long videoId);
}
