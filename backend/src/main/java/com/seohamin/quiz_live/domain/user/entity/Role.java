package com.seohamin.quiz_live.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    NOT_REGISTERED("ROLE_NOT_REGISTERED", "회원가입이 끝나지 않은 유저"),
    STUDENT("ROLE_STUDENT", "학생"),
    PROFESSOR("ROLE_PROFESSOR", "교수"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
