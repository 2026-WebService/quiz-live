import { useEffect, useState } from 'react'
import { mockCourses, mockUsers } from './mocks/data'
import CourseListPage from './pages/CourseListPage'
import QuestionManagePage from './pages/QuestionManagePage'
import type { CourseResponse, Role } from './types'

// 해시 기반 간단한 라우팅: #/courses, #/courses/{id}/questions, ?role=STUDENT
function parseRoute() {
  const match = window.location.hash.match(/^#\/courses\/(\d+)\/questions/)
  return match ? { page: 'questions' as const, courseId: Number(match[1]) } : { page: 'courses' as const }
}

export default function App() {
  const initialRole = new URLSearchParams(window.location.search).get('role') === 'STUDENT' ? 'STUDENT' : 'PROFESSOR'
  const [role, setRole] = useState<Role>(initialRole)
  const [route, setRoute] = useState(parseRoute)
  const user = mockUsers[role]

  useEffect(() => {
    const onHash = () => setRoute(parseRoute())
    window.addEventListener('hashchange', onHash)
    return () => window.removeEventListener('hashchange', onHash)
  }, [])

  const openCourse = (course: CourseResponse) => { window.location.hash = `#/courses/${course.courseId}/questions` }
  const goHome = () => { window.location.hash = '#/courses' }
  const course = route.page === 'questions' ? mockCourses.find((c) => c.courseId === route.courseId) : undefined

  return (
    <>
      <header className="topbar">
        <span className="logo">Quiz<b>Live</b></span>
        <div className="role-switch" role="group" aria-label="보기 역할">
          {(['PROFESSOR', 'STUDENT'] as const).map((r) => (
            <button key={r} className={role === r ? 'active' : ''} onClick={() => { setRole(r); goHome() }}>
              {r === 'PROFESSOR' ? '교수' : '학생'}
            </button>
          ))}
        </div>
        <span className="user">{user.name} 님 <small className="muted">(Mock 로그인)</small></span>
      </header>
      <main>
        {course && role === 'PROFESSOR'
          ? <QuestionManagePage course={course} onBack={goHome} />
          : <CourseListPage key={role} user={user} onOpenCourse={openCourse} />}
      </main>
    </>
  )
}
