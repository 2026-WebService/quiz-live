package com.seohamin.quiz_live.domain.user.service;

import com.seohamin.quiz_live.domain.user.dto.UserResponseDto;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.user.repository.UserRepository;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getUser() {

        // oauth 및 인증 구현 전까지는 임시로 이렇게 해둠
        return UserResponseDto.of(userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST)));
    }

    // 다른 도메인 서비스에서 현재 사용자 엔티티가 필요할 때 사용
    public User getUserEntity(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }
}
