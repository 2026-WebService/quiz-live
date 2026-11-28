package com.seohamin.quiz_live.support;

import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.course.repository.CourseRepository;
import com.seohamin.quiz_live.domain.user.dto.OauthUserRequestDto;
import com.seohamin.quiz_live.domain.user.entity.Role;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.user.repository.UserRepository;
import com.seohamin.quiz_live.domain.video.entity.Video;
import com.seohamin.quiz_live.domain.video.entity.VideoStatus;
import com.seohamin.quiz_live.domain.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.UUID;

@TestComponent
@RequiredArgsConstructor
public class TestFixtures {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;

    public User user(final Role role) {
        final String sub = UUID.randomUUID().toString();
        final User user = new User(new OauthUserRequestDto(sub, sub + "@example.com", "nick", "테스트", null, null));
        user.updateRole(role);
        return userRepository.save(user);
    }

    public Course course(final User professor, final String joinCode) {
        return courseRepository.save(new Course("웹서비스개발", null, professor, joinCode));
    }

    public Video readyVideo(final Course course) {
        return videoRepository.save(new Video(course, "3주차 자료구조", null, VideoStatus.READY, "/hls/master.m3u8"));
    }
}
