import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { BadgeCheck, Briefcase, Code, FileText, Sparkles, TrendingUp, UserRound, Zap, Award, CalendarClock } from 'lucide-react';
import { useAuth } from '../../../app/providers/AuthProvider.jsx';
import { dashboardService } from '../services/dashboardService.js';
import { DashboardCard } from '../components/DashboardCard.jsx';
import { CareerHealthCard } from '../components/CareerHealthCard.jsx';
import { ActivityChart } from '../components/ActivityChart.jsx';
import { LearningProgressCard } from '../components/LearningProgressCard.jsx';
import { NotificationsCard } from '../components/NotificationsCard.jsx';
import { ActivityFeedCard } from '../components/ActivityFeedCard.jsx';
import { QuickActionsCard } from '../components/QuickActionsCard.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { normalizeDashboardData } from '../utils/normalizeDashboardData.js';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Link } from 'react-router-dom';

export function DashboardPage() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      setError('');
      const rawData = await dashboardService.getSummary();
      setDashboardData(normalizeDashboardData(rawData));
    } catch (error) {
      console.error('Failed to load dashboard:', error);
      setError(error.response?.data?.message || 'Dashboard data is unavailable.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <section className="space-y-6">
        <Panel className="p-6">
          <div className="h-24 animate-pulse rounded-2xl bg-slate-200/80 dark:bg-slate-800/80" />
        </Panel>
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="h-32 animate-pulse rounded-2xl bg-slate-200/80 dark:bg-slate-800/80" />
          ))}
        </div>
      </section>
    );
  }

  const iconMap = {
    profileCompletion: UserRound,
    resumes: FileText,
    applications: Briefcase,
    activity: Sparkles,
    notifications: BadgeCheck,
    projects: Code,
    certifications: Award,
    learning: BookOpen,
    coding: Code,
  };

  const quickActionIcons = {
    'Complete Profile': UserRound,
    'Create Resume': FileText,
    'Add Project': Code,
    'Track Internship': Briefcase,
  };

  return (
    <section className="space-y-6">
      {error && <Alert type="error">{error} <button type="button" className="ml-2 font-semibold underline" onClick={loadDashboard}>Retry</button></Alert>}
      <Panel className="overflow-hidden p-6">
        <div className="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
          <div className="max-w-2xl">
            <div className="inline-flex items-center gap-2 rounded-full border border-brand-200 bg-brand-50 px-3 py-1 text-sm font-medium text-brand-700 dark:border-brand-900/60 dark:bg-brand-950/30 dark:text-brand-300">
              <Sparkles size={16} />
              Welcome back, {user?.fullName || 'there'}
            </div>
            <h1 className="mt-4 text-3xl font-semibold tracking-tight text-slate-950 dark:text-slate-50 sm:text-4xl">
              Your career momentum is building.
            </h1>
            <p className="mt-3 text-base text-slate-600 dark:text-slate-400">
              {dashboardData?.user?.profileSetupCompleted
                ? 'You are making steady progress across projects, skills, and opportunities. Keep pushing forward.'
                : 'Complete your profile setup to unlock tailored recommendations and a sharper career view.'}
            </p>
          </div>
          <div className="rounded-2xl border border-slate-200 bg-slate-50/80 p-4 text-sm text-slate-600 dark:border-slate-700 dark:bg-slate-800/70 dark:text-slate-300">
            <div className="flex items-center gap-2 font-medium text-slate-900 dark:text-slate-100">
              <CalendarClock size={16} className="text-emerald-500" />
              Recorded activity, last 7 days
            </div>
            <p className="mt-2 text-3xl font-semibold text-slate-950 dark:text-slate-50">
              {dashboardData?.activityTrend?.reduce((total, point) => total + point.value, 0) ?? 0}
            </p>
            <p className="mt-1">Events recorded in CareerNest.</p>
          </div>
        </div>
      </Panel>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
        {dashboardData?.statistics?.map((stat, index) => (
          <DashboardCard
            key={stat.key}
            title={stat.label}
            value={stat.value}
            unit={stat.unit}
            description={stat.description}
            color={stat.color}
            icon={iconMap[stat.key] || TrendingUp}
            delay={index * 0.05}
          />
        ))}
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        {dashboardData?.careerHealth && <CareerHealthCard health={dashboardData.careerHealth} delay={0.3} />}
        {dashboardData?.activityTrend && <ActivityChart data={dashboardData.activityTrend.map((item) => ({ label: item.label, value: item.value }))} delay={0.35} />}
      </div>

      <div className="grid gap-6 lg:grid-cols-[0.95fr_1.05fr]">
        {dashboardData?.learningProgress && <LearningProgressCard progress={dashboardData.learningProgress} delay={0.45} />}
        {dashboardData?.quickActions && <QuickActionsCard actions={dashboardData.quickActions.map((action) => ({ ...action, icon: quickActionIcons[action.title] || Zap }))} delay={0.5} />}
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {dashboardData?.notifications && <NotificationsCard notifications={dashboardData.notifications} delay={0.55} />}
        {dashboardData?.activityFeed && <ActivityFeedCard activities={dashboardData.activityFeed} delay={0.6} />}
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <Panel className="p-5">
          <h2 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Applications by status</h2>
          {dashboardData?.applicationStatus?.length ? (
            <ul className="mt-4 space-y-3">
              {dashboardData.applicationStatus.map((item) => (
                <li key={item.label} className="flex items-center justify-between text-sm">
                  <span className="text-slate-600 dark:text-slate-400">{item.label}</span>
                  <span className="font-semibold tabular-nums">{item.value}</span>
                </li>
              ))}
            </ul>
          ) : <p className="mt-3 text-sm text-slate-500">No applications recorded.</p>}
          <Link className="mt-4 inline-flex text-sm font-medium text-brand-700 hover:underline dark:text-brand-300" to="/internships">Open applications</Link>
        </Panel>
        <Panel className="p-5">
          <h2 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Recent projects</h2>
          {dashboardData?.recentProjects?.length ? (
            <ul className="mt-4 space-y-3">
              {dashboardData.recentProjects.map((item) => (
                <li key={`${item.title}-${item.updatedAt}`} className="border-b border-slate-100 pb-3 last:border-0 dark:border-slate-800">
                  <p className="font-medium">{item.title}</p>
                  <p className="text-xs text-slate-500">{item.status}{item.technologySummary ? ` · ${item.technologySummary}` : ''}</p>
                </li>
              ))}
            </ul>
          ) : <p className="mt-3 text-sm text-slate-500">No projects recorded.</p>}
          <Link className="mt-4 inline-flex text-sm font-medium text-brand-700 hover:underline dark:text-brand-300" to="/projects">Open projects</Link>
        </Panel>
        <Panel className="p-5">
          <h2 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Upcoming tasks and milestones</h2>
          {dashboardData?.upcomingTasks?.length ? (
            <ul className="mt-4 space-y-3">
              {dashboardData.upcomingTasks.map((task, index) => (
                <li key={`${task.type}-${task.title}-${task.dueAt}-${index}`} className="border-b border-slate-100 pb-3 last:border-0 dark:border-slate-800">
                  <p className="font-medium">{task.title}</p>
                  <p className="text-xs text-slate-500">{task.organization || task.type} · {task.dueAt ? new Date(task.dueAt).toLocaleDateString() : ''}</p>
                  {task.route && <Link className="mt-1 inline-flex text-xs font-medium text-brand-700 hover:underline dark:text-brand-300" to={task.route}>View</Link>}
                </li>
              ))}
            </ul>
          ) : <p className="mt-3 text-sm text-slate-500">No upcoming dates recorded.</p>}
          <Link className="mt-4 inline-flex text-sm font-medium text-brand-700 hover:underline dark:text-brand-300" to="/timeline">Open timeline</Link>
        </Panel>
      </div>
    </section>
  );
}
