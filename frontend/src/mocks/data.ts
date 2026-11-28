import type { CourseResponse, QuestionResponse, User, VideoSummary } from '../types'

// Backend 연동 전까지 화면 확인용 Mock Data
export const mockUsers: Record<Role, User> = {
  PROFESSOR: { userId: 1, name: '김교수', role: 'PROFESSOR' },
  STUDENT: { userId: 2, name: '이학생', role: 'STUDENT' },
}
type Role = User['role']

export const mockCourses: CourseResponse[] = [
  {
    courseId: 1, title: '웹서비스개발', description: '2026-2 · 화 3-4교시',
    professorId: 1, professorName: '김교수', joinCode: 'VCKSBP', createdAt: '2026-09-01T00:00:00Z',
  },
  {
    courseId: 2, title: '자료구조', description: '2026-2 · 목 1-2교시',
    professorId: 1, professorName: '김교수', joinCode: 'H7QM2D', createdAt: '2026-09-02T00:00:00Z',
  },
  {
    courseId: 3, title: '운영체제', description: '2026-2 · 금 5-6교시',
    professorId: 4, professorName: '최교수', joinCode: 'R9XK4T', createdAt: '2026-09-03T00:00:00Z',
  },
]

// 학생(이학생)이 참여 중인 수업
export const mockStudentCourseIds = [1, 3]

export const mockVideos: VideoSummary[] = [
  { videoId: 1, courseId: 1, title: '3주차 자료구조 복습', status: 'READY', durationSec: 2710 },
  { videoId: 2, courseId: 1, title: '4주차 REST API 설계', status: 'TRANSCODING', durationSec: 3120 },
  { videoId: 3, courseId: 2, title: '1주차 스택과 큐', status: 'READY', durationSec: 2405 },
]

export const mockQuestions: QuestionResponse[] = [
  {
    questionId: 1, videoId: 1, type: 'MCQ', title: '스택의 특징', content: '스택에 대한 설명으로 옳은 것은?',
    timeLimitSec: 60, choices: ['FIFO 구조', 'LIFO 구조', '임의 접근'], answerIndex: 1, createdAt: '2026-11-20T01:00:00Z',
  },
  {
    questionId: 2, videoId: 1, type: 'CODE', title: '두 수의 합', content: '두 정수를 입력받아 합을 출력하세요.',
    timeLimitSec: 300, languages: ['PYTHON', 'C', 'JAVA'],
    testCases: [
      { input: '1 2', expectedOutput: '3', sample: true },
      { input: '-5 10', expectedOutput: '5', sample: false },
    ],
    createdAt: '2026-11-20T01:05:00Z',
  },
  {
    questionId: 3, videoId: 1, type: 'DRAWING', title: '이진 트리 그리기', content: '1~7을 순서대로 넣은 이진 탐색 트리를 그리세요.',
    timeLimitSec: 180, maxScore: 10, createdAt: '2026-11-20T01:10:00Z',
  },
]
