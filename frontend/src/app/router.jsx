import { createBrowserRouter, Navigate } from 'react-router-dom';
import { LoginPage } from '../features/auth/pages/LoginPage.jsx';
import { SignupPage } from '../features/auth/pages/SignupPage.jsx';
import { ForgotPasswordPage } from '../features/auth/pages/ForgotPasswordPage.jsx';
import { ResetPasswordPage } from '../features/auth/pages/ResetPasswordPage.jsx';
import { VerifyEmailPage } from '../features/auth/pages/VerifyEmailPage.jsx';
import { RoleSelectionPage } from '../features/auth/pages/RoleSelectionPage.jsx';
import { DashboardPage } from '../features/dashboard/pages/DashboardPage.jsx';
import { ProjectsPage } from '../features/projects/pages/ProjectsPage.jsx';
import { ProjectFormPage } from '../features/projects/pages/ProjectFormPage.jsx';
import { ProjectDetailPage } from '../features/projects/pages/ProjectDetailPage.jsx';
import { ProtectedRoute } from '../components/layout/ProtectedRoute.jsx';
import { DashboardLayout } from '../components/layout/DashboardLayout.jsx';
import { AdminLayout } from '../components/layout/AdminLayout.jsx';
import { AdminRoute } from '../components/layout/AdminRoute.jsx';
import { ProfileSetupPage } from '../features/onboarding/pages/ProfileSetupPage.jsx';
import { SessionsPage } from '../features/sessions/pages/SessionsPage.jsx';
import { CertificationsPage } from '../features/certifications/pages/CertificationsPage.jsx';
import { InternshipsPage } from '../features/internships/pages/InternshipsPage.jsx';
import { LearningPage } from '../features/learning/pages/LearningPage.jsx';
import { CodingProfilesPage } from '../features/coding/pages/CodingProfilesPage.jsx';
import { TimelinePage } from '../features/timeline/pages/TimelinePage.jsx';
import { CareerHealthPage } from '../features/health/pages/CareerHealthPage.jsx';
import { AssistantPage } from '../features/assistant/pages/AssistantPage.jsx';
import { AdminPage } from '../features/admin/pages/AdminPage.jsx';
import { ResumesPage } from '../features/resumes/pages/ResumesPage.jsx';
import { ResumeFormPage } from '../features/resumes/pages/ResumeFormPage.jsx';
import { ResumeDetailPage } from '../features/resumes/pages/ResumeDetailPage.jsx';
import { AdminDashboardPage } from '../features/admin/pages/AdminDashboardPage.jsx';
import { AdminUsersPage } from '../features/admin/pages/AdminUsersPage.jsx';
import { AdminAnalyticsPage } from '../features/admin/pages/AdminAnalyticsPage.jsx';
import { AdminReportsPage } from '../features/admin/pages/AdminReportsPage.jsx';
import { AdminAuditLogsPage } from '../features/admin/pages/AdminAuditLogsPage.jsx';
import { AdminSystemHealthPage } from '../features/admin/pages/AdminSystemHealthPage.jsx';
import { AdminSettingsPage } from '../features/admin/pages/AdminSettingsPage.jsx';
import { NotificationsPage } from '../features/notifications/pages/NotificationsPage.jsx';

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/role-selection" replace /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/signup', element: <SignupPage /> },
  { path: '/forgot-password', element: <ForgotPasswordPage /> },
  { path: '/reset-password', element: <ResetPasswordPage /> },
  { path: '/verify-email', element: <VerifyEmailPage /> },
  { path: '/role-selection', element: <RoleSelectionPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <DashboardLayout />,
        children: [
          { path: '/dashboard', element: <DashboardPage /> },
          { path: '/profile-setup', element: <ProfileSetupPage /> },
          { path: '/sessions', element: <SessionsPage /> },
          { path: '/projects', element: <ProjectsPage /> },
          { path: '/projects/new', element: <ProjectFormPage /> },
          { path: '/projects/:id', element: <ProjectDetailPage /> },
          { path: '/projects/:id/edit', element: <ProjectFormPage /> },
          { path: '/resumes', element: <ResumesPage /> },
          { path: '/resumes/new', element: <ResumeFormPage /> },
          { path: '/resumes/:id', element: <ResumeDetailPage /> },
          { path: '/resumes/:id/edit', element: <ResumeFormPage /> },
          { path: '/certifications', element: <CertificationsPage /> },
          { path: '/internships', element: <InternshipsPage /> },
          { path: '/learning', element: <LearningPage /> },
          { path: '/coding-profiles', element: <CodingProfilesPage /> },
          { path: '/timeline', element: <TimelinePage /> },
          { path: '/career-health', element: <CareerHealthPage /> },
          { path: '/assistant', element: <AssistantPage /> },
          { path: '/notifications', element: <NotificationsPage /> },
        ],
      },
      {
        element: <AdminRoute />,
        children: [
          {
            element: <AdminLayout />,
            children: [
              { path: '/admin', element: <AdminDashboardPage /> },
              { path: '/admin/users', element: <AdminUsersPage /> },
              { path: '/admin/analytics', element: <AdminAnalyticsPage /> },
              { path: '/admin/reports', element: <AdminReportsPage /> },
              { path: '/admin/audit-logs', element: <AdminAuditLogsPage /> },
              { path: '/admin/system-health', element: <AdminSystemHealthPage /> },
              { path: '/admin/settings', element: <AdminSettingsPage /> },
            ],
          },
        ],
      },
    ],
  },
]);
