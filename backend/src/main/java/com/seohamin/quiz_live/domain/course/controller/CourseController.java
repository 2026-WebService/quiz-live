package com.seohamin.quiz_live.domain.course.controller;

import com.seohamin.quiz_live.domain.course.dto.CourseCreateRequest;
import com.seohamin.quiz_live.domain.course.dto.CourseJoinRequest;
import com.seohamin.quiz_live.domain.course.dto.CourseResponse;
import com.seohamin.quiz_live.domain.course.service.CourseService;
import com.seohamin.quiz_live.global.auth.TempAuthHeader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Course", description = "수업 생성·참여")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "수업 생성 (교수)", description = "6자리 참여 코드가 발급된다.")
    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(
            @Parameter(description = TempAuthHeader.DESCRIPTION)
            @RequestHeader(TempAuthHeader.USER_ID) final Long userId,
            @Valid @RequestBody final CourseCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(userId, request));
    }

    @Operation(summary = "참여 코드로 수업 참여 (학생)")
    @PostMapping("/courses/join")
    public ResponseEntity<CourseResponse> joinCourse(
            @Parameter(description = TempAuthHeader.DESCRIPTION)
            @RequestHeader(TempAuthHeader.USER_ID) final Long userId,
            @Valid @RequestBody final CourseJoinRequest request
    ) {
        return ResponseEntity.ok(courseService.joinCourse(userId, request));
    }
}
