package com.seohamin.quiz_live.domain.user.dto;

public record OauthUserRequestDto(
        String providerUserId,
        String email,
        String nickname,
        String name,
        String profileImage,
        String refreshToken
) {
}
