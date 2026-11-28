package com.seohamin.quiz_live.global.exception.handler;

import com.seohamin.quiz_live.global.exception.CustomException;
import com.seohamin.quiz_live.global.exception.constants.ExceptionCode;
import com.seohamin.quiz_live.global.exception.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    // 커스텀으로 만든 예외들 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(final CustomException ex) {

        if (ex.getCause() != null) {
            log.error("EXCEPTION CODE {}, message: {}", ex.getExceptionCode().name(), ex.getMessage(), ex.getCause());
        }

        final ExceptionResponse response = new ExceptionResponse(ex.getExceptionCode());

        return ResponseEntity.status(ex.getExceptionCode().getHttpStatus()).body(response);
    }

    // @Valid 검증 실패시 발생하는 예외 처리
    // ResponseEntityExceptionHandler에 이미 존재해서 override함
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        final ExceptionResponse response = new ExceptionResponse(ExceptionCode.INVALID_REQUEST);

        return ResponseEntity.status(ExceptionCode.INVALID_REQUEST.getHttpStatus()).body(response);
    }

    // JSON 형식 오류, 없는 Enum 값(예: type: "ESSAY") 등 요청 본문을 읽을 수 없을 때
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        return toResponse(ExceptionCode.INVALID_REQUEST);
    }

    // 경로 변수·헤더 타입 불일치 (예: /questions/abc)
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            final TypeMismatchException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        return toResponse(ExceptionCode.INVALID_REQUEST);
    }

    // 임시 인증 헤더(X-User-Id)가 없으면 401, 그 밖의 바인딩 오류는 400
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            final ServletRequestBindingException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        if (ex instanceof MissingRequestHeaderException) {
            return toResponse(ExceptionCode.UNAUTHORIZED);
        }
        return toResponse(ExceptionCode.INVALID_REQUEST);
    }

    private ResponseEntity<Object> toResponse(final ExceptionCode exceptionCode) {
        return ResponseEntity.status(exceptionCode.getHttpStatus()).body(new ExceptionResponse(exceptionCode));
    }

    // 나머지 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllException(final Exception ex) {
        log.error("INTERNAL SERVER ERROR: {}", ex.getMessage(), ex);

        final ExceptionResponse response = new ExceptionResponse(ExceptionCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.internalServerError().body(response);
    }
}
