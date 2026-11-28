import { mockCourses, mockQuestions, mockStudentCourseIds, mockVideos } from '../mocks/data'
import type { ApiError, CourseResponse, QuestionCreateRequest, QuestionResponse, User, VideoSummary } from '../types'

// 실제 API(/api/v1/...)와 같은 함수 시그니처로 두고, Backend 연동 시 fetch 구현으로 교체한다.
const delay = <T,>(value: T) => new Promise<T>((resolve) => setTimeout(() => resolve(structuredClone(value)), 150))
const fail = (httpStatus: string, code: string, message: string): Promise<never> =>
  Promise.reject({ httpStatus, code, message } satisfies ApiError)

let courses = [...mockCourses]
let joined = [...mockStudentCourseIds]
let questions = [...mockQuestions]
let nextQuestionId = questions.length + 1

export async function getMyCourses(user: User): Promise<CourseResponse[]> {
  return delay(user.role === 'PROFESSOR'
    ? courses.filter((c) => c.professorId === user.userId)
    : courses.filter((c) => joined.includes(c.courseId)))
}

// POST /api/v1/courses/join
export async function joinCourse(user: User, joinCode: string): Promise<CourseResponse> {
  const code = joinCode.trim().toUpperCase()
  if (code.length !== 6) return fail('400 BAD_REQUEST', 'INVALID_REQUEST', '참여 코드는 6자리입니다.')
  const course = courses.find((c) => c.joinCode === code)
  if (!course) return fail('404 NOT_FOUND', 'NOT_FOUND', '해당 참여 코드의 수업이 없습니다.')
  if (user.role !== 'STUDENT') return fail('403 FORBIDDEN', 'FORBIDDEN', '학생만 참여할 수 있습니다.')
  if (joined.includes(course.courseId)) return fail('409 CONFLICT', 'ALREADY_JOINED_COURSE', '이미 참여한 수업입니다.')
  joined = [...joined, course.courseId]
  return delay(course)
}

// POST /api/v1/courses
export async function createCourse(user: User, title: string, description: string): Promise<CourseResponse> {
  if (!title.trim()) return fail('400 BAD_REQUEST', 'INVALID_REQUEST', '수업 이름을 입력하세요.')
  const created: CourseResponse = {
    courseId: courses.length + 1, title, description: description || null,
    professorId: user.userId, professorName: user.name,
    joinCode: Math.random().toString(36).slice(2, 8).toUpperCase(), createdAt: new Date().toISOString(),
  }
  courses = [...courses, created]
  return delay(created)
}

export async function getVideos(courseId: number): Promise<VideoSummary[]> {
  return delay(mockVideos.filter((v) => v.courseId === courseId))
}

// GET /api/v1/videos/{videoId}/questions
export async function getQuestions(videoId: number): Promise<QuestionResponse[]> {
  return delay(questions.filter((q) => q.videoId === videoId))
}

// POST /api/v1/videos/{videoId}/questions — 서버와 같은 유형별 검증
export async function createQuestion(videoId: number, req: QuestionCreateRequest): Promise<QuestionResponse> {
  if (!req.title.trim() || !req.content.trim()) return fail('400 BAD_REQUEST', 'INVALID_REQUEST', '제목과 내용을 입력하세요.')
  if (req.type === 'MCQ') {
    const n = req.choices?.length ?? 0
    if (n < 2 || n > 10 || req.answerIndex === undefined || req.answerIndex < 0 || req.answerIndex >= n) {
      return fail('400 BAD_REQUEST', 'INVALID_QUESTION_OPTION', '보기는 2~10개, 정답은 보기 중 하나여야 합니다.')
    }
  }
  const created: QuestionResponse = { ...req, questionId: nextQuestionId++, videoId, createdAt: new Date().toISOString() }
  questions = [...questions, created]
  return delay(created)
}

// DELETE /api/v1/questions/{questionId}
export async function deleteQuestion(questionId: number): Promise<void> {
  questions = questions.filter((q) => q.questionId !== questionId)
  await delay(null)
}
