// Backend DTO(CourseResponse, QuestionResponse 등)와 같은 모양으로 맞춘 타입
export type Role = 'STUDENT' | 'PROFESSOR'
export type QuestionType = 'MCQ' | 'CODE' | 'DRAWING'
export type Language = 'PYTHON' | 'C' | 'JAVA'
export type VideoStatus = 'PENDING' | 'TRANSCODING' | 'READY' | 'FAILED'

export interface User {
  userId: number
  name: string
  role: Role
}

export interface CourseResponse {
  courseId: number
  title: string
  description: string | null
  professorId: number
  professorName: string
  joinCode: string
  createdAt: string
}

export interface VideoSummary {
  videoId: number
  courseId: number
  title: string
  status: VideoStatus
  durationSec: number
}

export interface TestCase {
  input: string
  expectedOutput: string
  sample: boolean
}

export interface QuestionResponse {
  questionId: number
  videoId: number
  type: QuestionType
  title: string
  content: string
  timeLimitSec: number
  choices?: string[]
  answerIndex?: number
  languages?: Language[]
  testCases?: TestCase[]
  maxScore?: number
  createdAt: string
}

export type QuestionCreateRequest = Omit<QuestionResponse, 'questionId' | 'videoId' | 'createdAt'>

export interface ApiError {
  httpStatus: string
  code: string
  message: string
}
