package com.seohamin.quiz_live.domain.user.dto;

import com.seohamin.quiz_live.domain.user.entity.Role;
import com.seohamin.quiz_live.domain.user.entity.User;

public record UserResponseDto(
        Long userId,
        String email,
        String name,
        String profileImageUrl,
        Role role
) {

    public static UserResponseDto of(final User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getRole()
        );
    }
}
