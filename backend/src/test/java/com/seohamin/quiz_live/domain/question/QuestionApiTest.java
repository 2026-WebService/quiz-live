package com.seohamin.quiz_live.domain.question;

import com.seohamin.quiz_live.domain.course.entity.Course;
import com.seohamin.quiz_live.domain.user.entity.Role;
import com.seohamin.quiz_live.domain.user.entity.User;
import com.seohamin.quiz_live.domain.video.entity.Video;
import com.seohamin.quiz_live.global.auth.TempAuthHeader;
import com.seohamin.quiz_live.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 보고서 5.4절 Swagger 테스트 시나리오를 HTTP 수준에서 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestFixtures.class)
class QuestionApiTest {

    private static final String MCQ_BODY = """
            {
              "type": "MCQ",
              "title": "스택의 특징",
              "content": "스택에 대한 설명으로 옳은 것은?",
              "timeLimitSec": 60,
              "choices": ["FIFO", "LIFO", "랜덤"],
              "answerIndex": 1
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestFixtures fixtures;

    private User professor;
    private Video video;

    @BeforeEach
    void setUp() {
        professor = fixtures.user(Role.PROFESSOR);
        Course course = fixtures.course(professor, "ABC234");
        video = fixtures.readyVideo(course);
    }

    @Test
    void 문제_등록_수정_삭제_흐름() throws Exception {
        String location = mockMvc.perform(post("/api/v1/videos/{videoId}/questions", video.getId())
                        .header(TempAuthHeader.USER_ID, professor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MCQ_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").isNumber())
                .andExpect(jsonPath("$.answerIndex").value(1))
                .andExpect(jsonPath("$.languages").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        long questionId = Long.parseLong(location.replaceAll(".*\"questionId\":(\\d+).*", "$1"));

        mockMvc.perform(put("/api/v1/questions/{id}", questionId)
                        .header(TempAuthHeader.USER_ID, professor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "큐의 특징", "content": "큐 설명", "timeLimitSec": 90,
                                 "choices": ["FIFO", "LIFO"], "answerIndex": 0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("큐의 특징"))
                .andExpect(jsonPath("$.timeLimitSec").value(90));

        mockMvc.perform(get("/api/v1/videos/{videoId}/questions", video.getId())
                        .header(TempAuthHeader.USER_ID, professor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/api/v1/questions/{id}", questionId)
                        .header(TempAuthHeader.USER_ID, professor.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/questions/{id}", questionId)
                        .header(TempAuthHeader.USER_ID, professor.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void 제목이_없으면_400_INVALID_REQUEST() throws Exception {
        mockMvc.perform(post("/api/v1/videos/{videoId}/questions", video.getId())
                        .header(TempAuthHeader.USER_ID, professor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MCQ_BODY.replace("\"스택의 특징\"", "\"\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void 보기가_하나면_400_INVALID_QUESTION_OPTION() throws Exception {
        mockMvc.perform(post("/api/v1/videos/{videoId}/questions", video.getId())
                        .header(TempAuthHeader.USER_ID, professor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MCQ_BODY.replace("[\"FIFO\", \"LIFO\", \"랜덤\"]", "[\"FIFO\"]")
                                .replace("\"answerIndex\": 1", "\"answerIndex\": 0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_QUESTION_OPTION"));
    }

    @Test
    void 없는_문제_유형이면_400() throws Exception {
        mockMvc.perform(post("/api/v1/videos/{videoId}/questions", video.getId())
                        .header(TempAuthHeader.USER_ID, professor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MCQ_BODY.replace("\"MCQ\"", "\"ESSAY\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void 사용자_헤더가_없으면_401() throws Exception {
        mockMvc.perform(get("/api/v1/videos/{videoId}/questions", video.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void 다른_교수는_403() throws Exception {
        User other = fixtures.user(Role.PROFESSOR);
        mockMvc.perform(get("/api/v1/videos/{videoId}/questions", video.getId())
                        .header(TempAuthHeader.USER_ID, other.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void 스웨거_문서가_생성된다() throws Exception {
        mockMvc.perform(get("/api/swagger-ui/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/videos/{videoId}/questions']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/courses/join']").exists());
    }
}
