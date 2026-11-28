package com.seohamin.quiz_live.domain.question.controller;

import com.seohamin.quiz_live.domain.question.dto.QuestionCreateRequest;
import com.seohamin.quiz_live.domain.question.dto.QuestionResponse;
import com.seohamin.quiz_live.domain.question.dto.QuestionUpdateRequest;
import com.seohamin.quiz_live.domain.question.service.QuestionService;
import com.seohamin.quiz_live.global.auth.TempAuthHeader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Question", description = "영상별 문제 CRUD (교수)")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "문제 등록", description = "type에 따라 MCQ는 choices·answerIndex, CODE는 languages·testCases, DRAWING은 maxScore가 필요하다.")
    @PostMapping("/videos/{videoId}/questions")
    public ResponseEntity<QuestionResponse> createQuestion(
            @Parameter(description = TempAuthHeader.DESCRIPTION)
            @RequestHeader(TempAuthHeader.USER_ID) final Long userId,
            @PathVariable final Long videoId,
            @Valid @RequestBody final QuestionCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(userId, videoId, request));
    }

    @Operation(summary = "영상별 문제 목록 조회", description = "등록 순으로 정렬한다.")
    @GetMapping("/videos/{videoId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @Parameter(description = TempAuthHeader.DESCRIPTION)
            @RequestHeader(TempAuthHeader.USER_ID) final Long userId,
            @PathVariable final Long videoId
    ) {
        return ResponseEntity.ok(questionService.getQuestions(userId, videoId));
    }

    @Operation(summary = "문제 단건 조회")
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(
            @Parameter(description = TempAuthHeader.DESCRIPTION)
            @RequestHeader(TempAuthHeader.USER_ID) final Long userId,
            @PathVariable final Long questionId
    ) {
        return ResponseEntity.ok(questionService.getQuestion(userId, questionId));
    }

    @Operation(summary = "문제 수정", description = "문제 유형은 바꿀 수 없고, 기존 유형에 해당하는 필드만 반영한다.")
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @Parameter(description = TempAuthHeader.DESCRIPTION)
            @RequestHeader(TempAuthHeader.USER_ID) final Long userId,
            @PathVariable final Long questionId,
            @Valid @RequestBody final QuestionUpdateRequest request
    ) {
        return ResponseEntity.ok(questionService.updateQuestion(userId, questionId, request));
    }

    @Operation(summary = "문제 삭제")
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = TempAuthHeader.DESCRIPTION)
            @RequestHeader(TempAuthHeader.USER_ID) final Long userId,
            @PathVariable final Long questionId
    ) {
        questionService.deleteQuestion(userId, questionId);
        return ResponseEntity.noContent().build();
    }
}
