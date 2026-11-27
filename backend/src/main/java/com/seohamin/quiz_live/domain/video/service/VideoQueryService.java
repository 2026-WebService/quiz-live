package com.seohamin.quiz_live.domain.video.service;

import com.seohamin.quiz_live.domain.video.entity.Video;
import com.seohamin.quiz_live.domain.video.entity.VideoStatus;
import com.seohamin.quiz_live.domain.video.repository.VideoRepository;
import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoQueryService {

    private final VideoRepository videoRepository;

    public Video getVideo(final Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND));
    }

    public Video getReadyVideoForCourse(final Long courseId, final Long videoId) {
        Video video = getVideo(videoId);
        if (!video.getCourse().getId().equals(courseId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN);
        }
        if (video.getStatus() != VideoStatus.READY) {
            throw new CustomException(ExceptionCode.VIDEO_NOT_READY);
        }
        return video;
    }
}
