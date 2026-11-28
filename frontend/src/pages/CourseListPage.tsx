import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { createCourse, getMyCourses, joinCourse } from '../api/mockApi'
import type { ApiError, CourseResponse, User } from '../types'

interface Props {
  user: User
  onOpenCourse: (course: CourseResponse) => void
}

export default function CourseListPage({ user, onOpenCourse }: Props) {
  const [courses, setCourses] = useState<CourseResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [joinCode, setJoinCode] = useState('')
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [message, setMessage] = useState<{ kind: 'ok' | 'error'; text: string } | null>(null)

  useEffect(() => {
    let alive = true
    getMyCourses(user).then((list) => {
      if (alive) { setCourses(list); setLoading(false) }
    })
    return () => { alive = false }
  }, [user])

  const isProfessor = user.role === 'PROFESSOR'

  async function handleJoin(e: FormEvent) {
    e.preventDefault()
    try {
      const course = await joinCourse(user, joinCode)
      setCourses((prev) => [...prev, course])
      setJoinCode('')
      setMessage({ kind: 'ok', text: `'${course.title}' 수업에 참여했습니다.` })
    } catch (err) {
      const apiError = err as ApiError
      setMessage({ kind: 'error', text: `${apiError.httpStatus} · ${apiError.message}` })
    }
  }

  async function handleCreate(e: FormEvent) {
    e.preventDefault()
    try {
      const course = await createCourse(user, title, description)
      setCourses((prev) => [...prev, course])
      setTitle('')
      setDescription('')
      setMessage({ kind: 'ok', text: `수업을 만들었습니다. 참여 코드: ${course.joinCode}` })
    } catch (err) {
      setMessage({ kind: 'error', text: (err as ApiError).message })
    }
  }

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <h1>내 수업</h1>
          <p className="muted">{isProfessor ? '내가 개설한 수업입니다. 수업을 선택해 영상별 문제를 관리하세요.' : '참여 중인 수업입니다. 교수님께 받은 6자리 참여 코드로 수업에 들어갈 수 있습니다.'}</p>
        </div>
      </div>

      {isProfessor ? (
        <form className="panel inline-form" onSubmit={handleCreate}>
          <strong>새 수업 만들기</strong>
          <input placeholder="수업 이름" value={title} onChange={(e) => setTitle(e.target.value)} />
          <input placeholder="설명 (선택)" value={description} onChange={(e) => setDescription(e.target.value)} />
          <button type="submit" className="primary">만들기</button>
        </form>
      ) : (
        <form className="panel inline-form" onSubmit={handleJoin}>
          <strong>수업 참여</strong>
          <input className="code-input" placeholder="참여 코드 6자리" maxLength={6} value={joinCode}
            onChange={(e) => setJoinCode(e.target.value.toUpperCase())} />
          <button type="submit" className="primary">참여하기</button>
        </form>
      )}
      {message && <p className={`notice ${message.kind}`}>{message.text}</p>}

      {loading ? <p className="muted">불러오는 중…</p> : (
        <div className="card-grid">
          {courses.map((course) => (
            <article key={course.courseId} className="card">
              <h2>{course.title}</h2>
              <p className="muted">{course.description ?? '설명 없음'}</p>
              <dl>
                <div><dt>교수</dt><dd>{course.professorName}</dd></div>
                {isProfessor && <div><dt>참여 코드</dt><dd className="join-code">{course.joinCode}</dd></div>}
              </dl>
              <div className="card-actions">
                {isProfessor
                  ? <button className="primary" onClick={() => onOpenCourse(course)}>문제 관리</button>
                  : <button disabled title="14주차 라이브 방 기능과 함께 연결 예정">라이브 방 입장</button>}
              </div>
            </article>
          ))}
          {courses.length === 0 && <p className="muted">아직 수업이 없습니다.</p>}
        </div>
      )}
    </div>
  )
}
