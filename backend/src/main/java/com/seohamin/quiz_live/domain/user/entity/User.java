package com.seohamin.quiz_live.domain.user.entity;

import com.seohamin.quiz_live.domain.user.dto.OauthUserRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * 유저 (구글 OAuth 계정 정보 포함)
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_user_id", columnNames = {"provider_user_id"})
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //구글 회원 식별자 (sub)
    @Column(length = 255, nullable = false)
    private String providerUserId;

    //이메일 (구글에서 받아옴)
    @Column(length = 320, nullable = true)
    private String email;

    //닉네임 (OAuth에서 받아옴, 중복 허용)
    @Column(length = 255, nullable = false)
    private String nickname;

    //프로필 사진 url (OAuth에서 받아옴)
    @Column(length = 2048, nullable = true)
    private String profileImage;

    //실제 이름 (OAuth에서 받아옴)
    @Column(length = 255, nullable = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;

    //구글에서 제공하는 리프레시 토큰 (unlink시 필요함)
    @Column(length = 4100, nullable = true)
    private String refreshToken;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant linkedAt;

    @CreatedDate
    @Column(nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    //구글 oauth 회원가입용 생성자
    public User(final OauthUserRequestDto oauthUserRequestDto){
        this.providerUserId = oauthUserRequestDto.providerUserId();
        this.email = oauthUserRequestDto.email();
        this.nickname = oauthUserRequestDto.nickname();
        this.profileImage = oauthUserRequestDto.profileImage();
        this.name = oauthUserRequestDto.name();
        this.refreshToken = oauthUserRequestDto.refreshToken();
        this.role = Role.NOT_REGISTERED;
    }

    //닉네임 변경
    public void updateNickname(final String newNickname){
        this.nickname = newNickname;
    }

    //프로필 사진 변경
    public void updateProfileImage(final String newProfileImage){
        this.profileImage = newProfileImage;
    }

    //역할 변경 (회원가입 시 학생/교수 선택)
    public void updateRole(final Role role){
        this.role = role;
    }

    //리프레시 토큰 변경
    public void updateRefreshToken(final String refreshToken){
        this.refreshToken = refreshToken;
    }
}
