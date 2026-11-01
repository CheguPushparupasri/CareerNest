import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ShieldCheck, UserRound, KeyRound, Clock, FileText, Briefcase, BookOpen, Code, Award, TrendingUp, Activity, Bell, Zap } from 'lucide-react';
import { useAuth } from '../../../app/providers/AuthProvider.jsx';
import { dashboardService } from '../../dashboard/services/dashboardService.js';
import { DashboardCard } from '../../dashboard/components/DashboardCard.jsx';
import { CareerHealthCard } from '../../dashboard/components/CareerHealthCard.jsx';
import { ActivityChart } from '../../dashboard/components/ActivityChart.jsx';
import { LearningProgressCard } from '../../dashboard/components/LearningProgressCard.jsx';
import { NotificationsCard } from '../../dashboard/components/NotificationsCard.jsx';
import { ActivityFeedCard } from '../../dashboard/components/ActivityFeedCard.jsx';
import { QuickActionsCard } from '../../dashboard/components/QuickActionsCard.jsx';

export function DashboardPage() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState(null);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      const data = await dashboardService.getSummary();
      setDashboardData(data);
    } catch (error) {
      console.error('Failed to load dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <section>
        <div className="mb-6">
          <h1 className="text-3xl font-bold">Loading...</h1>
        </div>
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="h-32 animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800" />
          ))}
        </div>
      </section>
    );
  }

  const iconMap = {
    profileCompletion: UserRound,
    resumes: FileText,
    applications: Briefcase,
    activity: Activity,
    notifications: Bell,
  };

  const quickActionIcons = {
    'Complete Profile': UserRound,
    'Create Resume': FileText,
    'Add Project': Code,
    'Track Internship': Briefcase,
  };

  return (
    <section>
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Welcome, {user?.fullName}</h1>
        <p className="mt-1 text-slate-500 dark:text-slate-400">
          {dashboardData?.user?.profileSetupCompleted 
            ? 'Your career journey is progressing well. Keep building your skills and experience.'
            : 'Complete your profile setup to unlock personalized career insights and recommendations.'}
        </p>
      </div>

      {/* Statistics Cards */}
      <div className="mb-6 grid gap-4 md:grid-cols-2 xl:grid-cols-5">
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

      <div className="grid gap-6 lg:grid-cols-2">
        {/* Career Health Score */}
        {dashboardData?.careerHealth && (
          <CareerHealthCard health={dashboardData.careerHealth} delay={0.3} />
        )}

        {/* Activity Trend Chart */}
        {dashboardData?.activityTrend && (
          <ActivityChart 
            data={dashboardData.activityTrend.map(item => ({
              label: item.label,
              value: item.value
            }))} 
            delay={0.4} 
          />
        />

        {/* Learning Progress */}
        {dashboardData?.learningProgress && (
          <LearningProgressCard 
            progress={dashboardData.learningProgress} 
            delay={0.5} 
          />
        )}

        {/* Notifications */}
        {dashboardData?.notifications && (
          <NotificationsCard 
            notifications={dashboardData.notifications} 
            delay={0.6} 
          />
        )}

        {/* Activity Feed */}
        {dashboardData?.activityFeed && (
          <ActivityFeedCard 
            activities={dashboardData.activityFeed} 
            delay={0.7} 
          />
        )}

        {/* Quick Actions */}
        {dashboardData?.quickActions && (
          <QuickActionsCard 
            actions={dashboardData.quickActions.map(action => ({
              ...action,
              icon: quickActionIcons[action.title] || Zap
            }))} 
            delay={0.8} 
          />
        )}
      </div>
    </section>
  );
}
