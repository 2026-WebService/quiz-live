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

    public static final int MAX_TEXT_BYTES = 65535;
    // UTF-8 한 글자가 최대 4바이트이므로 TEXT 컬럼에 항상 들어가는 글자 수
    public static final int MAX_TEXT_LENGTH = 16000;

    // 길이를 지정하지 않으면 MariaDB에서 TINYTEXT(255바이트)로 생성되므로 TEXT(64KB) 범위로 지정
    @Lob
    @Column(name = "input_text", nullable = false, length = MAX_TEXT_BYTES)
    private String input;

    @Lob
    @Column(name = "expected_output", nullable = false, length = MAX_TEXT_BYTES)
    private String expectedOutput;

    @Column(nullable = false)
    private boolean sample;

    public QuestionTestCase(final String input, final String expectedOutput, final boolean sample) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.sample = sample;
    }
}
