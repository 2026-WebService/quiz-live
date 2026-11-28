import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { createQuestion, deleteQuestion, getQuestions, getVideos } from '../api/mockApi'
import type { ApiError, CourseResponse, QuestionResponse, QuestionType, VideoSummary } from '../types'

const TYPE_LABEL: Record<QuestionType, string> = { MCQ: '객관식', CODE: '코드', DRAWING: '그림' }
const STATUS_LABEL: Record<VideoSummary['status'], string> = {
  PENDING: '대기', TRANSCODING: '변환 중', READY: '준비됨', FAILED: '실패',
}

const formatDuration = (sec: number) => `${Math.floor(sec / 60)}분 ${sec % 60}초`

interface Props {
  course: CourseResponse
  onBack: () => void
}

export default function QuestionManagePage({ course, onBack }: Props) {
  const [videos, setVideos] = useState<VideoSummary[]>([])
  const [selected, setSelected] = useState<VideoSummary | null>(null)
  const [questions, setQuestions] = useState<QuestionResponse[]>([])

  const [type, setType] = useState<QuestionType>('MCQ')
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [timeLimitSec, setTimeLimitSec] = useState(60)
  const [choices, setChoices] = useState(['', ''])
  const [answerIndex, setAnswerIndex] = useState(0)
  const [maxScore, setMaxScore] = useState(10)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getVideos(course.courseId).then((list) => {
      setVideos(list)
      setSelected(list.find((v) => v.status === 'READY') ?? list[0] ?? null)
    })
  }, [course.courseId])

  useEffect(() => {
    if (selected) getQuestions(selected.videoId).then(setQuestions)
  }, [selected])

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (!selected) return
    try {
      const created = await createQuestion(selected.videoId, {
        type, title, content, timeLimitSec,
        ...(type === 'MCQ' ? { choices: choices.filter((c) => c.trim()), answerIndex } : {}),
        ...(type === 'DRAWING' ? { maxScore } : {}),
        ...(type === 'CODE' ? { languages: ['PYTHON'], testCases: [{ input: '1 2', expectedOutput: '3', sample: true }] } : {}),
      })
      setQuestions((prev) => [...prev, created])
      setTitle(''); setContent(''); setChoices(['', '']); setAnswerIndex(0); setError(null)
    } catch (err) {
      const apiError = err as ApiError
      setError(`${apiError.httpStatus} ${apiError.code} · ${apiError.message}`)
    }
  }

  async function handleDelete(questionId: number) {
    await deleteQuestion(questionId)
    setQuestions((prev) => prev.filter((q) => q.questionId !== questionId))
  }

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <button className="link" onClick={onBack}>← 내 수업</button>
          <h1>{course.title} · 문제 관리</h1>
          <p className="muted">영상을 고르고 수업 중 출제할 문제를 미리 등록합니다. 변환이 끝난 영상만 라이브 방에 쓸 수 있습니다.</p>
        </div>
      </div>

      <div className="split">
        <aside className="panel video-list">
          <strong>강의 영상</strong>
          {videos.map((video) => (
            <button key={video.videoId}
              className={`video-item ${selected?.videoId === video.videoId ? 'active' : ''}`}
              onClick={() => setSelected(video)}>
              <span>{video.title}</span>
              <small className="muted">{formatDuration(video.durationSec)}</small>
              <span className={`badge status-${video.status.toLowerCase()}`}>{STATUS_LABEL[video.status]}</span>
            </button>
          ))}
        </aside>

        <section className="question-area">
          <div className="panel">
            <strong>등록된 문제 {questions.length}개</strong>
            <ul className="question-list">
              {questions.map((q, i) => (
                <li key={q.questionId}>
                  <div className="q-head">
                    <span className={`badge type-${q.type.toLowerCase()}`}>{TYPE_LABEL[q.type]}</span>
                    <b>{i + 1}. {q.title}</b>
                    <span className="muted">제한 {q.timeLimitSec}초</span>
                    <button className="danger small" onClick={() => handleDelete(q.questionId)}>삭제</button>
                  </div>
                  <p>{q.content}</p>
                  {q.choices && (
                    <ol className="choices">
                      {q.choices.map((c, idx) => <li key={idx} className={idx === q.answerIndex ? 'answer' : ''}>{c}{idx === q.answerIndex && ' ✓'}</li>)}
                    </ol>
                  )}
                  {q.languages && <p className="muted">언어: {q.languages.join(', ')} · 테스트케이스 {q.testCases?.length ?? 0}개 (공개 {q.testCases?.filter((t) => t.sample).length ?? 0}개)</p>}
                  {q.maxScore !== undefined && <p className="muted">배점 {q.maxScore}점 · 교수 직접 채점</p>}
                </li>
              ))}
            </ul>
          </div>

          <form className="panel question-form" onSubmit={handleSubmit}>
            <strong>문제 추가</strong>
            <div className="row">
              <label>유형
                <select value={type} onChange={(e) => setType(e.target.value as QuestionType)}>
                  <option value="MCQ">객관식</option>
                  <option value="CODE">코드</option>
                  <option value="DRAWING">그림</option>
                </select>
              </label>
              <label className="grow">제목<input value={title} onChange={(e) => setTitle(e.target.value)} /></label>
              <label>제한 시간(초)<input type="number" min={1} max={3600} value={timeLimitSec} onChange={(e) => setTimeLimitSec(Number(e.target.value))} /></label>
            </div>
            <label>내용<textarea rows={2} value={content} onChange={(e) => setContent(e.target.value)} /></label>
            {type === 'MCQ' && (
              <div className="choice-editor">
                {choices.map((choice, idx) => (
                  <label key={idx} className="choice-row">
                    <input type="radio" name="answer" checked={answerIndex === idx} onChange={() => setAnswerIndex(idx)} />
                    <input placeholder={`보기 ${idx + 1}`} value={choice}
                      onChange={(e) => setChoices((prev) => prev.map((c, i) => (i === idx ? e.target.value : c)))} />
                  </label>
                ))}
                {choices.length < 10 && <button type="button" className="small" onClick={() => setChoices((prev) => [...prev, ''])}>+ 보기 추가</button>}
              </div>
            )}
            {type === 'DRAWING' && (
              <label>배점<input type="number" min={1} value={maxScore} onChange={(e) => setMaxScore(Number(e.target.value))} /></label>
            )}
            {type === 'CODE' && <p className="muted">언어·테스트케이스 편집은 Backend 연동과 함께 추가 예정 (Mock에서는 Python, 샘플 1개로 등록)</p>}
            {error && <p className="notice error">{error}</p>}
            <button type="submit" className="primary" disabled={!selected}>등록</button>
          </form>
        </section>
      </div>
    </div>
  )
}
