package com.seohamin.quiz_live.domain.question.entity;

import com.seohamin.quiz_live.domain.video.entity.Video;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, updatable = false)
    private QuestionType type;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 4000, nullable = false)
    private String content;

    @Column(name = "time_limit_sec", nullable = false)
    private Integer timeLimitSec;

    @ElementCollection
    @CollectionTable(name = "question_choices", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "choice_order")
    @Column(name = "choice_text", length = 1000, nullable = false)
    private List<String> choices = new ArrayList<>();

    @Column(name = "answer_index")
    private Integer answerIndex;

    @ElementCollection
    @CollectionTable(name = "question_languages", joinColumns = @JoinColumn(name = "question_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "language", length = 20, nullable = false)
    private Set<Language> languages = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "question_test_cases", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "test_case_order")
    private List<QuestionTestCase> testCases = new ArrayList<>();

    @Column(name = "max_score")
    private Integer maxScore;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Question(
            final Video video,
            final QuestionType type,
            final String title,
            final String content,
            final Integer timeLimitSec
    ) {
        this.video = video;
        this.type = type;
        this.title = title;
        this.content = content;
        this.timeLimitSec = timeLimitSec;
    }

    public static Question mcq(
            final Video video,
            final String title,
            final String content,
            final Integer timeLimitSec,
            final List<String> choices,
            final Integer answerIndex
    ) {
        Question question = new Question(video, QuestionType.MCQ, title, content, timeLimitSec);
        question.choices.addAll(choices);
        question.answerIndex = answerIndex;
        return question;
    }

    public static Question code(
            final Video video,
            final String title,
            final String content,
            final Integer timeLimitSec,
            final Set<Language> languages,
            final List<QuestionTestCase> testCases
    ) {
        Question question = new Question(video, QuestionType.CODE, title, content, timeLimitSec);
        question.languages.addAll(languages);
        question.testCases.addAll(testCases);
        return question;
    }

    public static Question drawing(
            final Video video,
            final String title,
            final String content,
            final Integer timeLimitSec,
            final Integer maxScore
    ) {
        Question question = new Question(video, QuestionType.DRAWING, title, content, timeLimitSec);
        question.maxScore = maxScore;
        return question;
    }
}
