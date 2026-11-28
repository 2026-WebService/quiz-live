package com.seohamin.quiz_live.global.auth;

/**
 * 로그인(OAuth + JWT) 구현 전까지 현재 사용자를 식별하기 위한 임시 헤더.
 * Spring Security 적용 후 Authentication에서 사용자 ID를 꺼내도록 교체한다.
 */
public final class TempAuthHeader {

    public static final String USER_ID = "X-User-Id";
    public static final String DESCRIPTION = "임시 인증: 로그인 구현 전까지 현재 사용자 ID를 헤더로 전달";

    private TempAuthHeader() {
    }
}
