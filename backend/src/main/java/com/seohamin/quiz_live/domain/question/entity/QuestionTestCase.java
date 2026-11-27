package com.seohamin.quiz_live.domain.question.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionTestCase {

    @Lob
    @Column(name = "input_text", nullable = false)
    private String input;

    @Lob
    @Column(name = "expected_output", nullable = false)
    private String expectedOutput;

    @Column(nullable = false)
    private boolean sample;

    public QuestionTestCase(final String input, final String expectedOutput, final boolean sample) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.sample = sample;
    }
}
